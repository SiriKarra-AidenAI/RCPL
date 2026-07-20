-- ---------------------------------------------------------------------------
-- Bring `grievances` in line with the frontend's Grievance contract
-- (RCPL-Angular-Frontend/src/app/mock/grievances.ts): channel + priority were
-- missing, and the single free-text `summary` is split into `subject`/`detail`.
-- ---------------------------------------------------------------------------
ALTER TABLE grievances ADD channel VARCHAR2(24);
ALTER TABLE grievances ADD priority VARCHAR2(16);
ALTER TABLE grievances ADD subject VARCHAR2(300);
ALTER TABLE grievances ADD detail VARCHAR2(2000);

UPDATE grievances SET subject = summary WHERE subject IS NULL;

ALTER TABLE grievances DROP COLUMN summary;
