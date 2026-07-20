package com.rcpl.platform.gtm;

import java.util.List;
import java.util.Set;

import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.auth.DataScopeService;
import com.rcpl.platform.gtm.GtmDtos.AreaDto;
import com.rcpl.platform.gtm.GtmDtos.CityDto;
import com.rcpl.platform.gtm.GtmDtos.CoverageResponse;
import com.rcpl.platform.gtm.GtmDtos.DbDto;
import com.rcpl.platform.gtm.GtmDtos.FactorDto;
import com.rcpl.platform.gtm.GtmDtos.StateDto;
import com.rcpl.platform.gtm.entity.GtmArea;
import com.rcpl.platform.gtm.entity.GtmCity;
import com.rcpl.platform.gtm.entity.GtmDb;
import com.rcpl.platform.gtm.entity.GtmState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** GTM coverage: region-scoped state→city→area→DB tree, factor definitions, and bulk import. */
@Service
public class GtmService {

    private static final String ENTITY = "gtm_coverage";

    private final GtmStateRepository stateRepo;
    private final GtmCityRepository cityRepo;
    private final GtmAreaRepository areaRepo;
    private final GtmDbRepository dbRepo;
    private final GtmFactorRepository factorRepo;
    private final DataScopeService dataScopeService;

    public GtmService(GtmStateRepository stateRepo, GtmCityRepository cityRepo, GtmAreaRepository areaRepo,
                      GtmDbRepository dbRepo, GtmFactorRepository factorRepo, DataScopeService dataScopeService) {
        this.stateRepo = stateRepo;
        this.cityRepo = cityRepo;
        this.areaRepo = areaRepo;
        this.dbRepo = dbRepo;
        this.factorRepo = factorRepo;
        this.dataScopeService = dataScopeService;
    }

    @Transactional(readOnly = true)
    public CoverageResponse coverage(CurrentUser user) {
        Set<String> allowed = dataScopeService.allowedStateCodes(user, ENTITY); // null = all
        List<StateDto> states = stateRepo.findAllByOrderByNameAsc().stream()
                .filter(s -> allowed == null || allowed.contains(s.getCode()))
                .map(this::stateTree)
                .toList();
        List<FactorDto> factors = factorRepo.findAllByOrderBySortOrderAsc().stream()
                .map(f -> new FactorDto(f.getFactorKey(), f.getLabel(), f.getSub(), f.getIcon(),
                        f.getPerTarget(), f.getDelta(), f.isMoney(), f.isExtra()))
                .toList();
        return new CoverageResponse(states, factors);
    }

    private StateDto stateTree(GtmState s) {
        List<CityDto> cities = cityRepo.findByStateCodeOrderByNameAsc(s.getCode()).stream()
                .map(c -> new CityDto(c.getName(), c.getTarget(), c.getActual(),
                        areaRepo.findByCityIdOrderByNameAsc(c.getId()).stream()
                                .map(a -> new AreaDto(a.getName(), a.getTarget(), a.getActual(),
                                        dbRepo.findByAreaIdOrderByNameAsc(a.getId()).stream()
                                                .map(d -> new DbDto(d.getName(), d.getDbType(), d.getStatus()))
                                                .toList()))
                                .toList()))
                .toList();
        return new StateDto(s.getCode(), s.getName(), s.getRegion(), s.getTarget(), s.getActual(), cities);
    }

    /** Bulk upsert of state trees (admin) — replaces each state's cities/areas/DBs. */
    @Transactional
    public int importStates(List<StateDto> states) {
        if (states == null) return 0;
        for (StateDto sd : states) {
            GtmState state = stateRepo.findById(sd.code()).orElseGet(GtmState::new);
            state.setCode(sd.code());
            state.setName(sd.name());
            state.setRegion(sd.region());
            if (sd.target() != null) state.setTarget(sd.target());
            if (sd.actual() != null) state.setActual(sd.actual());
            stateRepo.save(state);

            if (sd.cities() != null) {
                clearState(sd.code());
                for (CityDto cd : sd.cities()) {
                    GtmCity city = new GtmCity();
                    city.setStateCode(sd.code());
                    city.setName(cd.name());
                    city.setTarget(cd.target() == null ? 0 : cd.target());
                    city.setActual(cd.actual() == null ? 0 : cd.actual());
                    cityRepo.save(city);
                    if (cd.areas() != null) {
                        for (AreaDto ad : cd.areas()) {
                            GtmArea area = new GtmArea();
                            area.setCityId(city.getId());
                            area.setName(ad.name());
                            area.setTarget(ad.target() == null ? 0 : ad.target());
                            area.setActual(ad.actual() == null ? 0 : ad.actual());
                            areaRepo.save(area);
                            if (ad.dbs() != null) {
                                for (DbDto dd : ad.dbs()) {
                                    GtmDb db = new GtmDb();
                                    db.setAreaId(area.getId());
                                    db.setName(dd.name());
                                    db.setDbType(dd.type());
                                    db.setStatus(dd.status());
                                    dbRepo.save(db);
                                }
                            }
                        }
                    }
                }
            }
        }
        return states.size();
    }

    private void clearState(String stateCode) {
        for (GtmCity city : cityRepo.findByStateCodeOrderByNameAsc(stateCode)) {
            for (GtmArea area : areaRepo.findByCityIdOrderByNameAsc(city.getId())) {
                dbRepo.deleteAll(dbRepo.findByAreaIdOrderByNameAsc(area.getId()));
            }
            areaRepo.deleteAll(areaRepo.findByCityIdOrderByNameAsc(city.getId()));
        }
        cityRepo.deleteByStateCode(stateCode);
    }
}
