-- Essential reference data (NOT demo/mock business records): the role registry,
-- per-role screen permissions, row-level data-scope defaults, and analytics-section
-- visibility. Values mirror src/mock/roles.ts and src/components/shell/nav.ts, with
-- "manage" set to what each persona actually needs to do its job (e.g. Leadership
-- manages /approvals for final sign-off, ASE/ASM manages /leads to create leads).
-- The bootstrap admin USER is created at startup by BootstrapAdminRunner (needs a
-- real BCrypt hash), not here.

-- Roles ----------------------------------------------------------------------
INSERT INTO roles (code, label, color_var, blurb) VALUES ('ase_asm', 'ASE / ASM', '--p-ase', 'Scout candidates, submit recommendations, respond to flags.');
INSERT INTO roles (code, label, color_var, blurb) VALUES ('finance', 'Finance', '--p-finance', 'Review flagged financial criteria, approve or reject.');
INSERT INTO roles (code, label, color_var, blurb) VALUES ('channel_dev', 'Channel Development', '--p-channel', 'Review infrastructure & coverage, own DB onboarding.');
INSERT INTO roles (code, label, color_var, blurb) VALUES ('mdm', 'MDM', '--p-mdm', 'Verify documents, confirm & onboard partner records.');
INSERT INTO roles (code, label, color_var, blurb) VALUES ('leadership', 'Leadership', '--p-leadership', 'Read analytics, sign off appointments, share insights.');
INSERT INTO roles (code, label, color_var, blurb) VALUES ('admin', 'Admin', '--ai-strong', 'Configure templates, manage partner types & workflows.');

-- Screen access (view=1 for screens the persona sees; manage=1 where it acts) ---
-- ASE / ASM
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('ase_asm', '/dashboard', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('ase_asm', '/intake-inbox', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('ase_asm', '/document-authenticity', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('ase_asm', '/leads', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('ase_asm', '/approvals', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('ase_asm', '/communication', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('ase_asm', '/analytics', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('ase_asm', '/gtm-coverage', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('ase_asm', '/partners', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('ase_asm', '/audit-log', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('ase_asm', '/my-settings', 1, 1);

-- Finance
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('finance', '/dashboard', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('finance', '/approvals', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('finance', '/documents', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('finance', '/communication', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('finance', '/analytics', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('finance', '/audit-log', 1, 0);

-- Channel Development
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('channel_dev', '/dashboard', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('channel_dev', '/intake-inbox', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('channel_dev', '/document-authenticity', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('channel_dev', '/leads', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('channel_dev', '/new-application', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('channel_dev', '/approvals', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('channel_dev', '/documents', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('channel_dev', '/communication', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('channel_dev', '/grievances', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('channel_dev', '/analytics', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('channel_dev', '/gtm-coverage', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('channel_dev', '/partners', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('channel_dev', '/audit-log', 1, 0);

-- MDM
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('mdm', '/dashboard', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('mdm', '/approvals', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('mdm', '/documents', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('mdm', '/communication', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('mdm', '/partners', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('mdm', '/analytics', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('mdm', '/audit-log', 1, 0);

-- Leadership (manages /approvals for final sign-off, /reports to publish)
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('leadership', '/dashboard', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('leadership', '/approvals', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('leadership', '/analytics', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('leadership', '/gtm-coverage', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('leadership', '/reports', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('leadership', '/partners', 1, 0);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('leadership', '/audit-log', 1, 0);

-- Admin (manages everything it can see)
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/dashboard', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/intake-inbox', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/document-authenticity', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/leads', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/new-application', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/approvals', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/documents', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/communication', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/grievances', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/analytics', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/gtm-coverage', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/reports', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/partners', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/templates', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/settings', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/audit-log', 1, 1);
INSERT INTO role_screen_access (role_code, screen_path, can_view, can_manage) VALUES ('admin', '/my-settings', 1, 1);

-- Row-level data scope per role (entities: dashboard, partners, gtm_coverage, analytics) --
-- Channel Development is region-scoped; everyone else sees all by default.
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('ase_asm', 'dashboard', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('ase_asm', 'partners', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('ase_asm', 'gtm_coverage', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('ase_asm', 'analytics', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('finance', 'dashboard', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('finance', 'partners', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('finance', 'gtm_coverage', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('finance', 'analytics', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('channel_dev', 'dashboard', 'own_region');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('channel_dev', 'partners', 'own_region');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('channel_dev', 'gtm_coverage', 'own_region');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('channel_dev', 'analytics', 'own_region');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('mdm', 'dashboard', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('mdm', 'partners', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('mdm', 'gtm_coverage', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('mdm', 'analytics', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('leadership', 'dashboard', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('leadership', 'partners', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('leadership', 'gtm_coverage', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('leadership', 'analytics', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('admin', 'dashboard', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('admin', 'partners', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('admin', 'gtm_coverage', 'all');
INSERT INTO role_data_scope (role_code, entity, scope) VALUES ('admin', 'analytics', 'all');

-- Analytics section visibility (everyone sees all three by default) ------------
INSERT INTO role_analytics_section (role_code, section) VALUES ('ase_asm', 'overview');
INSERT INTO role_analytics_section (role_code, section) VALUES ('ase_asm', 'detail');
INSERT INTO role_analytics_section (role_code, section) VALUES ('ase_asm', 'efficiency');
INSERT INTO role_analytics_section (role_code, section) VALUES ('finance', 'overview');
INSERT INTO role_analytics_section (role_code, section) VALUES ('finance', 'detail');
INSERT INTO role_analytics_section (role_code, section) VALUES ('finance', 'efficiency');
INSERT INTO role_analytics_section (role_code, section) VALUES ('channel_dev', 'overview');
INSERT INTO role_analytics_section (role_code, section) VALUES ('channel_dev', 'detail');
INSERT INTO role_analytics_section (role_code, section) VALUES ('channel_dev', 'efficiency');
INSERT INTO role_analytics_section (role_code, section) VALUES ('mdm', 'overview');
INSERT INTO role_analytics_section (role_code, section) VALUES ('mdm', 'detail');
INSERT INTO role_analytics_section (role_code, section) VALUES ('mdm', 'efficiency');
INSERT INTO role_analytics_section (role_code, section) VALUES ('leadership', 'overview');
INSERT INTO role_analytics_section (role_code, section) VALUES ('leadership', 'detail');
INSERT INTO role_analytics_section (role_code, section) VALUES ('leadership', 'efficiency');
INSERT INTO role_analytics_section (role_code, section) VALUES ('admin', 'overview');
INSERT INTO role_analytics_section (role_code, section) VALUES ('admin', 'detail');
INSERT INTO role_analytics_section (role_code, section) VALUES ('admin', 'efficiency');
