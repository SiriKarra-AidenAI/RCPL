import { Routes } from '@angular/router'
import { authGuard } from './components/auth/auth.guard'
import { requireRole } from './components/auth/RequireRole'
import { ShellComponent } from './components/shell/Shell'
import { PlaceholderComponent } from './screens/placeholder.component'
import { ForbiddenComponent } from './screens/forbidden.component'
import { DocumentViewerComponent } from './screens/document-viewer.component'
import { ReportsComponent } from './screens/reports.component'
import { TemplatesComponent } from './screens/templates.component'

// Mirrors the original App.tsx route table. Screens not yet converted from React route to the
// already-converted PlaceholderComponent (title/blurb/bullets bound from route `data` via
// withComponentInputBinding()) so the app is fully navigable today — swap in the real component
// route-by-route as each one lands, same path, no restructuring needed.
//
// '/agents' is deliberately NOT routed here, matching the original App.tsx, which has it
// commented out ("needs a design pass") even though Agents.tsx/agents.component.ts exists.
export const routes: Routes = [
  {
    path: 'login',
    component: PlaceholderComponent,
    data: {
      title: 'Login',
      blurb: 'Sign in to the RCPL Partner Platform.',
      bullets: [
        'Email/SSO sign-in against the auth service',
        'Role-based redirect straight to your dashboard',
      ],
    },
  },
  // No Shell chrome — this is the "View full document" destination, opened in its own tab.
  {
    path: 'document-viewer',
    component: DocumentViewerComponent,
    canActivate: [authGuard],
  },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        component: PlaceholderComponent,
        data: {
          title: 'Dashboard',
          blurb: 'Your personal landing page — KPIs, journey tracker, and next best action.',
          bullets: [
            'Persona-specific KPIs and journey tracker',
            'AI-generated insight of the day',
            "One-click 'up next' recommended action",
            'Quick actions to jump straight into your queue',
          ],
        },
      },
      {
        path: 'intake-inbox',
        component: PlaceholderComponent,
        data: {
          title: 'Intake Inbox',
          blurb: 'Where the Intake Agent surfaces new candidate emails and documents for review.',
          bullets: [
            'Live feed of incoming candidate emails/documents',
            'AI-extracted fields with confidence scores',
            'One click to promote an item into a Lead',
          ],
        },
      },
      {
        path: 'intake/:id',
        component: PlaceholderComponent,
        data: {
          title: 'Intake Review',
          blurb: "Review one intake item's extracted fields against its source document before creating a lead.",
          bullets: [
            'Field-by-field extracted vs. claimed comparison',
            'Source document viewer with highlighted matches',
            'Create Lead / forward / mark reviewed actions',
          ],
        },
      },
      {
        path: 'document-authenticity',
        component: PlaceholderComponent,
        data: {
          title: 'Document Authenticity',
          blurb: 'Flags forged or mismatched submitted documents before onboarding.',
          bullets: [
            'Claimed vs. extracted value mismatches',
            'Confidence-scored authenticity checks',
            'Escalation path for suspected forgeries',
          ],
        },
      },
      {
        path: 'leads',
        component: PlaceholderComponent,
        data: {
          title: 'Leads',
          blurb: 'The shortlist of distributor/vendor candidates the field team has created and compared.',
          bullets: [
            'Pipeline view across every lead stage',
            'Side-by-side candidate comparison',
            'Hand-off into New Application for scoring',
          ],
        },
      },
      {
        // Detail route reachable from within modules (not in the sidebar) — bypasses Shell's
        // module-access guard the same way it did in the original.
        path: 'distributor',
        component: PlaceholderComponent,
        data: {
          title: 'Distributor Profile',
          blurb: "A partner's full 360° profile — business, contact, performance, history, and documents.",
          bullets: [
            'Tabbed 360° partner view',
            'Clickable KPI tiles with a full breakdown',
            'Grievance and communication history in context',
          ],
        },
      },
      {
        path: 'new-application',
        component: PlaceholderComponent,
        data: {
          title: 'New Application',
          blurb: 'The onboarding wizard — score, compare, and appoint a new partner.',
          bullets: [
            'AI-ranked candidate recommendation',
            'Automated approval-matrix evaluation',
            'Raises a case automatically when a candidate is flagged',
          ],
        },
      },
      {
        path: 'approvals',
        component: PlaceholderComponent,
        data: {
          title: 'Approvals',
          blurb: 'The queue for cases flagged by the automated evaluation — Finance, Channel Development, MDM, and Leadership sign-off.',
          bullets: [
            'Per-role approval queue with SLA timers',
            'Required-document upload gates',
            'Bulk approve / reject with full audit trail',
          ],
        },
      },
      {
        path: 'documents',
        component: PlaceholderComponent,
        data: {
          title: 'Documents',
          blurb: 'Every document submitted across every case, with verification status.',
          bullets: [
            'Claimed vs. extracted verification status',
            'Filter by partner type, case, or document kind',
            'Optional Document Intelligence matching',
          ],
        },
      },
      {
        path: 'communication',
        component: PlaceholderComponent,
        data: {
          title: 'Communication',
          blurb: 'Threaded, per-case discussion — internal and partner-facing.',
          bullets: [
            'One thread per case or grievance',
            "Automatic 'next to reply' nudges",
            'Partner-facing nudges and holding replies',
          ],
        },
      },
      {
        path: 'grievances',
        component: PlaceholderComponent,
        data: {
          title: 'Grievances',
          blurb: 'The distributor grievance queue, owned by Trade Marketing.',
          bullets: [
            'Open / in-progress / resolved queue',
            'One-click holding reply to the distributor',
            "Full history surfaced on the partner's 360° profile",
          ],
        },
      },
      {
        path: 'analytics',
        component: PlaceholderComponent,
        data: {
          title: 'Analytics',
          blurb: 'Coverage, approval, and onboarding-efficiency metrics across the whole funnel.',
          bullets: [
            'Application trend and outcome mix',
            'Per-distributor performance scorecards',
            'Onboarding efficiency: funnel velocity, KYC, field force',
          ],
        },
      },
      {
        path: 'gtm-coverage',
        component: PlaceholderComponent,
        data: {
          title: 'GTM Coverage',
          blurb: 'State → city → area distributor coverage against target, on a live map.',
          bullets: [
            'Target vs. actual distributor counts by area',
            'Drill-down from state to town',
            'Counts derived live from the Partners directory',
          ],
        },
      },
      { path: 'reports', component: ReportsComponent },
      {
        path: 'partners',
        component: PlaceholderComponent,
        data: {
          title: 'Partners',
          blurb: 'The full partner directory — distributors, vendors, and their status.',
          bullets: [
            'Searchable, filterable partner directory',
            'Row-level data scoping by region/state',
            "Deep-link into each partner's 360° profile",
          ],
        },
      },
      { path: 'templates', component: TemplatesComponent, canActivate: [requireRole(['admin'])] },
      {
        path: 'settings',
        component: PlaceholderComponent,
        canActivate: [requireRole(['admin'])],
        data: {
          title: 'Admin & Settings',
          blurb: 'Super Admin controls — user directory, screen access, and data scoping per persona.',
          bullets: [
            'Manage the persona/user directory',
            'Per-persona screen access (View/Manage)',
            'Per-persona row-level data scope',
          ],
        },
      },
      {
        path: 'my-settings',
        component: PlaceholderComponent,
        data: {
          title: 'My Settings',
          blurb: 'Your personal inbox connection and SLA preferences.',
          bullets: [
            'Connect the mailbox the Intake Agent watches',
            'Configure your SLA review window',
            'Auto-forward unmatched intake toggle',
          ],
        },
      },
      // Audit trail is shared — every persona can read it; only Templates/Settings stay admin-gated.
      {
        path: 'audit-log',
        component: PlaceholderComponent,
        data: {
          title: 'Audit Log',
          blurb: 'The shared, cross-team trail of every real action taken in the app.',
          bullets: [
            'Every approval, onboarding, and export logged',
            'Shared across all personas',
            'Filter by actor, kind, or entity',
          ],
        },
      },
      // Not part of the original route table (Forbidden was rendered in place by RequireRole,
      // never navigated to by URL) — needed now that RequireRole is a redirecting route guard.
      { path: 'forbidden', component: ForbiddenComponent },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
]
