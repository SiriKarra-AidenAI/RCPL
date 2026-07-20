-- Reference/config data for the extended schema (not demo business records):
-- partner-type templates, onboarding thresholds, DB categories, infra factors,
-- GTM state-level coverage, and GTM factor definitions. Mirrors src/mock/templates.ts,
-- src/mock/onboarding.ts and src/mock/gtm.ts.

-- Partner types (template engine) -------------------------------------------
INSERT INTO partner_types (code, label, is_active, sort_order) VALUES ('distributor', 'Distributor (DB)', 1, 1);
INSERT INTO partner_types (code, label, is_active, sort_order) VALUES ('vendor', 'Vendor / Service Partner', 1, 2);
INSERT INTO partner_types (code, label, is_active, sort_order) VALUES ('logistics', 'Logistics Partner', 0, 3);
INSERT INTO partner_types (code, label, is_active, sort_order) VALUES ('copacker', 'Co-packer', 0, 4);

INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('distributor', 'GST Certificate', 1);
INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('distributor', 'FSSAI License', 2);
INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('distributor', 'Godown Proof', 3);
INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('distributor', 'DB Onboarding Form', 4);
INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('vendor', 'GST', 1);
INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('vendor', 'PAN', 2);
INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('vendor', 'Cancelled Cheque', 3);
INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('vendor', 'MSME', 4);
INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('vendor', 'ISO 9001', 5);
INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('vendor', 'Factory Audit Report', 6);
INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('logistics', 'GST', 1);
INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('logistics', 'Fleet Registration', 2);
INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('logistics', 'Insurance', 3);
INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('copacker', 'GST', 1);
INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('copacker', 'ISO', 2);
INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('copacker', 'Factory Audit', 3);
INSERT INTO partner_type_documents (partner_type_code, doc_name, sort_order) VALUES ('copacker', 'Manufacturing License', 4);

INSERT INTO partner_type_workflow (partner_type_code, step, sort_order) VALUES ('distributor', 'Evaluation', 1);
INSERT INTO partner_type_workflow (partner_type_code, step, sort_order) VALUES ('distributor', 'Finance (if flagged)', 2);
INSERT INTO partner_type_workflow (partner_type_code, step, sort_order) VALUES ('distributor', 'Channel Development (if flagged)', 3);
INSERT INTO partner_type_workflow (partner_type_code, step, sort_order) VALUES ('distributor', 'Document Check (optional)', 4);
INSERT INTO partner_type_workflow (partner_type_code, step, sort_order) VALUES ('vendor', 'Quality Review', 1);
INSERT INTO partner_type_workflow (partner_type_code, step, sort_order) VALUES ('vendor', 'Regulatory Check', 2);
INSERT INTO partner_type_workflow (partner_type_code, step, sort_order) VALUES ('vendor', 'Procurement ARC Match', 3);
INSERT INTO partner_type_workflow (partner_type_code, step, sort_order) VALUES ('vendor', 'MDM Document Check', 4);
INSERT INTO partner_type_workflow (partner_type_code, step, sort_order) VALUES ('logistics', 'To be configured', 1);
INSERT INTO partner_type_workflow (partner_type_code, step, sort_order) VALUES ('copacker', 'Reuses the Vendor quality-audit workflow', 1);

-- Onboarding config / scoring thresholds ------------------------------------
INSERT INTO onboarding_config (config_key, config_value) VALUES ('INFRA_THRESHOLD', 7.0);
INSERT INTO onboarding_config (config_key, config_value) VALUES ('REQUIRED_INVESTMENT', 144.6);
INSERT INTO onboarding_config (config_key, config_value) VALUES ('FIN_EVAL_PASS', 100);
INSERT INTO onboarding_config (config_key, config_value) VALUES ('EXPECTED_RCPL_TURNOVER', 41);
INSERT INTO onboarding_config (config_key, config_value) VALUES ('RBL_APPROVAL_THRESHOLD', 50);

-- DB categories -------------------------------------------------------------
INSERT INTO db_category (code, label, sort_order) VALUES ('GT DB (with CSO/DSM)', 'GT DB (with CSO/DSM)', 1);
INSERT INTO db_category (code, label, sort_order) VALUES ('GM Excl DB', 'GM Excl DB', 2);
INSERT INTO db_category (code, label, sort_order) VALUES ('Traders', 'Traders', 3);

-- Infra factors (the 8 scored per candidate) --------------------------------
INSERT INTO infra_item (item_key, label, sort_order) VALUES ('salesmen', 'Salesmen & delivery', 1);
INSERT INTO infra_item (item_key, label, sort_order) VALUES ('delivery', 'Delivery units', 2);
INSERT INTO infra_item (item_key, label, sort_order) VALUES ('godown', 'Godown with required space', 3);
INSERT INTO infra_item (item_key, label, sort_order) VALUES ('computer', 'Computer / operator availability', 4);
INSERT INTO infra_item (item_key, label, sort_order) VALUES ('reputation', 'Reputation in the marketplace', 5);
INSERT INTO infra_item (item_key, label, sort_order) VALUES ('coverage', 'Coverage of outlets regularly', 6);
INSERT INTO infra_item (item_key, label, sort_order) VALUES ('credit', 'Extending credit to the market', 7);
INSERT INTO infra_item (item_key, label, sort_order) VALUES ('involvement', 'Degree of personal involvement', 8);

-- GTM state-level coverage (drill-down cities/areas/DBs loaded via import) ---
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('AN', 'Andaman & Nicobar Islands', 'South', 8, 3);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('AP', 'Andhra Pradesh', 'South', 49, 11);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('AR', 'Arunachal Pradesh', 'East', 2, 3);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('AS', 'Assam', 'East', 39, 7);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('BR', 'Bihar', 'East', 27, 6);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('CG', 'Chhattisgarh', 'Central', 13, 7);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('CH', 'Chandigarh', 'North', 32, 4);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('DH', 'Dadra & Nagar Haveli and Daman & Diu', 'West', 6, 2);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('DL', 'Delhi (NCT)', 'North', 29, 6);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('GA', 'Goa', 'West', 15, 8);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('GJ', 'Gujarat', 'West', 32, 12);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('HP', 'Himachal Pradesh', 'North', 16, 6);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('HR', 'Haryana', 'North', 21, 8);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('JH', 'Jharkhand', 'East', 17, 6);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('JK', 'Jammu & Kashmir', 'North', 13, 8);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('KA', 'Karnataka', 'South', 23, 7);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('KL', 'Kerala', 'South', 63, 5);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('LD', 'Lakshadweep', 'South', 8, 1);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('MH', 'Maharashtra', 'West', 82, 18);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('ML', 'Meghalaya', 'East', 2, 3);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('MN', 'Manipur', 'East', 7, 4);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('MP', 'Madhya Pradesh', 'Central', 28, 7);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('MZ', 'Mizoram', 'East', 7, 4);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('NL', 'Nagaland', 'East', 4, 3);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('OD', 'Odisha', 'East', 19, 5);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('PB', 'Punjab', 'North', 18, 8);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('PY', 'Puducherry', 'South', 8, 4);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('RJ', 'Rajasthan', 'North', 39, 6);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('SK', 'Sikkim', 'East', 8, 4);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('TN', 'Tamil Nadu', 'South', 44, 4);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('TR', 'Tripura', 'East', 2, 5);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('TS', 'Telangana', 'South', 26, 5);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('UK', 'Uttarakhand', 'North', 14, 7);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('UP', 'Uttar Pradesh', 'North', 63, 12);
INSERT INTO gtm_state (code, name, region, target, actual) VALUES ('WB', 'West Bengal', 'East', 36, 12);

-- GTM factors ---------------------------------------------------------------
INSERT INTO gtm_factor (factor_key, label, sub, icon, per_target, delta, is_money, is_extra, sort_order) VALUES ('salesmen', 'Salesmen & delivery', 'Total appointed', 'user', 12.5, 0.252, 0, 0, 1);
INSERT INTO gtm_factor (factor_key, label, sub, icon, per_target, delta, is_money, is_extra, sort_order) VALUES ('delivery', 'Delivery units', 'Total appointed', 'target', 6.5, 0.162, 0, 0, 2);
INSERT INTO gtm_factor (factor_key, label, sub, icon, per_target, delta, is_money, is_extra, sort_order) VALUES ('godown', 'Godown with required space', 'Total appointed', 'documents', 1.4, 0.23, 0, 0, 3);
INSERT INTO gtm_factor (factor_key, label, sub, icon, per_target, delta, is_money, is_extra, sort_order) VALUES ('computer', 'Computer / operator availability', 'Total appointed', 'settings', 1.5, 0.333, 0, 0, 4);
INSERT INTO gtm_factor (factor_key, label, sub, icon, per_target, delta, is_money, is_extra, sort_order) VALUES ('credit', 'Credit limit availability', 'Total approved', 'analytics', 2.5, 0.27, 1, 0, 5);
INSERT INTO gtm_factor (factor_key, label, sub, icon, per_target, delta, is_money, is_extra, sort_order) VALUES ('outlets', 'Outlets covered regularly', 'Active beat coverage', 'leads', 60, 0.2, 0, 1, 6);
INSERT INTO gtm_factor (factor_key, label, sub, icon, per_target, delta, is_money, is_extra, sort_order) VALUES ('beats', 'Beat plans active', 'Published & running', 'dashboard', 3.2, 0.18, 0, 1, 7);
INSERT INTO gtm_factor (factor_key, label, sub, icon, per_target, delta, is_money, is_extra, sort_order) VALUES ('reputation', 'Reputation checks completed', 'Market references', 'approvals', 1, 0.3, 0, 1, 8);
