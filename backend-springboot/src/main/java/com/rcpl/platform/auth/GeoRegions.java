package com.rcpl.platform.auth;

import java.util.Map;

/**
 * State-code → macro-region and state-code → full-name maps, ported verbatim from the
 * frontend's src/mock/gtm.ts (REGION_OF + GTM_STATES) so row-level data scope resolves
 * identically on the server. Records store the state code (e.g. "MH"); a user's own state
 * is stored as a full name (e.g. "Maharashtra").
 */
public final class GeoRegions {

    private GeoRegions() {}

    public static final Map<String, String> REGION_OF = Map.ofEntries(
            Map.entry("AN", "South"), Map.entry("AP", "South"), Map.entry("AR", "East"),
            Map.entry("AS", "East"), Map.entry("BR", "East"), Map.entry("CG", "Central"),
            Map.entry("CH", "North"), Map.entry("DH", "West"), Map.entry("DL", "North"),
            Map.entry("GA", "West"), Map.entry("GJ", "West"), Map.entry("HP", "North"),
            Map.entry("HR", "North"), Map.entry("JH", "East"), Map.entry("JK", "North"),
            Map.entry("KA", "South"), Map.entry("KL", "South"), Map.entry("LA", "North"),
            Map.entry("LD", "South"), Map.entry("MH", "West"), Map.entry("ML", "East"),
            Map.entry("MN", "East"), Map.entry("MP", "Central"), Map.entry("MZ", "East"),
            Map.entry("NL", "East"), Map.entry("OD", "East"), Map.entry("PB", "North"),
            Map.entry("PY", "South"), Map.entry("RJ", "North"), Map.entry("SK", "East"),
            Map.entry("TN", "South"), Map.entry("TR", "East"), Map.entry("TS", "South"),
            Map.entry("UK", "North"), Map.entry("UP", "North"), Map.entry("WB", "East")
    );

    public static final Map<String, String> NAME_BY_CODE = Map.ofEntries(
            Map.entry("AN", "Andaman & Nicobar Islands"), Map.entry("AP", "Andhra Pradesh"),
            Map.entry("AR", "Arunachal Pradesh"), Map.entry("AS", "Assam"), Map.entry("BR", "Bihar"),
            Map.entry("CG", "Chhattisgarh"), Map.entry("CH", "Chandigarh"),
            Map.entry("DH", "Dadra & Nagar Haveli and Daman & Diu"), Map.entry("DL", "Delhi (NCT)"),
            Map.entry("GA", "Goa"), Map.entry("GJ", "Gujarat"), Map.entry("HP", "Himachal Pradesh"),
            Map.entry("HR", "Haryana"), Map.entry("JH", "Jharkhand"), Map.entry("JK", "Jammu & Kashmir"),
            Map.entry("KA", "Karnataka"), Map.entry("KL", "Kerala"), Map.entry("LA", "Ladakh"),
            Map.entry("LD", "Lakshadweep"), Map.entry("MH", "Maharashtra"), Map.entry("ML", "Meghalaya"),
            Map.entry("MN", "Manipur"), Map.entry("MP", "Madhya Pradesh"), Map.entry("MZ", "Mizoram"),
            Map.entry("NL", "Nagaland"), Map.entry("OD", "Odisha"), Map.entry("PB", "Punjab"),
            Map.entry("PY", "Puducherry"), Map.entry("RJ", "Rajasthan"), Map.entry("SK", "Sikkim"),
            Map.entry("TN", "Tamil Nadu"), Map.entry("TR", "Tripura"), Map.entry("TS", "Telangana"),
            Map.entry("UK", "Uttarakhand"), Map.entry("UP", "Uttar Pradesh"), Map.entry("WB", "West Bengal")
    );
}
