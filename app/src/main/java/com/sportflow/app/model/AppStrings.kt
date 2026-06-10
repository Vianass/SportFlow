package com.sportflow.app.model

data class AppStrings(
    // ── Navigation ──────────────────────────────────────────────────────────
    val navHome: String,
    val navEvents: String,
    val navSubscriptions: String,
    val navProfile: String,
    val navStats: String,
    val navCreateEvent: String,

    // ── Common ───────────────────────────────────────────────────────────────
    val signOut: String,
    val search: String,
    val filters: String,
    val viewAll: String,
    val filter: String,
    val calendar: String,
    val open: String,
    val editPhoto: String,

    // ── Profile shared ───────────────────────────────────────────────────────
    val sectionAccountData: String,
    val sectionSettings: String,
    val fieldEmail: String,
    val fieldPhone: String,
    val fieldLocation: String,
    val settingNotifications: String,
    val settingNotificationsSubtitle: String,
    val settingPrivacy: String,
    val settingPrivacySubtitle: String,
    val settingChangePassword: String,
    val settingChangePasswordSubtitle: String,
    val settingSubscriptionPayments: String,
    val settingSubscriptionPaymentsSubtitle: String,
    val settingLanguage: String,
    val settingLanguageSubtitle: String,

    // ── User Profile ─────────────────────────────────────────────────────────
    val userRoleBadge: String,

    // ── Admin Profile ────────────────────────────────────────────────────────
    val adminRoleBadge: String,

    // ── Organizador Profile ──────────────────────────────────────────────────
    val organizadorRoleBadge: String,
    val sectionAccountSettings: String,
    val sectionSecuritySupport: String,
    val settingEditProfile: String,
    val settingEditProfileSubtitle: String,
    val settingOrgData: String,
    val settingOrgDataSubtitle: String,
    val settingNotificationsOrgSubtitle: String,
    val settingSecurity: String,
    val settingSecuritySubtitle: String,
    val settingHelpCenter: String,
    val settingHelpCenterSubtitle: String,
    val metricActiveTournaments: String,
    val metricRegisteredAthletes: String,

    // ── UserHomeScreen ───────────────────────────────────────────────────────
    val liveEventsTitle: String,
    val upcomingEventsTitle: String,
    val promoBannerLine1: String,
    val promoBannerLine2: String,
    val promoBannerLine3: String,

    // ── UserEventsScreen ─────────────────────────────────────────────────────
    val eventsPageLabel: String,
    val eventsPageTitle1: String,
    val eventsPageTitle2: String,
    val eventsPageSubtitle: String,
    val searchPlaceholder: String,
    val spotsLeft: String,
    val soldOut: String,
    val registerNow: String,
    val unavailable: String,

    // ── UserSubscriptionsScreen ──────────────────────────────────────────────
    val subsTitle: String,
    val subsSubtitle: String,
    val filterAll: String,
    val filterActive: String,
    val filterCompleted: String,
    val statusConfirmed: String,
    val statusPending: String,
    val statusFinished: String,
    val btnViewDetails: String,
    val btnCompletePayment: String,
    val btnWithdraw: String,

    // ── AdminDashboard ───────────────────────────────────────────────────────
    val searchTooltip: String,

    // ── Language Picker Dialog ───────────────────────────────────────────────
    val languageDialogTitle: String,
    val languageDialogSubtitle: String,
    val languagePT: String,
    val languageEN: String,
)

val PortugueseStrings = AppStrings(
    // Navigation
    navHome = "INÍCIO",
    navEvents = "EVENTOS",
    navSubscriptions = "INSCRIÇÕES",
    navProfile = "PERFIL",
    navStats = "ESTATÍSTICAS",
    navCreateEvent = "CRIAR EVENTO",

    // Common
    signOut = "Terminar Sessão",
    search = "Pesquisar",
    filters = "FILTROS",
    viewAll = "Ver todos",
    filter = "Filtrar",
    calendar = "Calendário",
    open = "Abrir",
    editPhoto = "Editar Foto",

    // Profile shared
    sectionAccountData = "DADOS DA CONTA",
    sectionSettings = "CONFIGURAÇÕES",
    fieldEmail = "Email",
    fieldPhone = "Telemóvel",
    fieldLocation = "Localização",
    settingNotifications = "Notificações",
    settingNotificationsSubtitle = "Preferências de alerta",
    settingPrivacy = "Privacidade",
    settingPrivacySubtitle = "Controlo de visibilidade",
    settingChangePassword = "Alterar Palavra-passe",
    settingChangePasswordSubtitle = "Atualizar credenciais",
    settingSubscriptionPayments = "Assinatura e Pagamentos",
    settingSubscriptionPaymentsSubtitle = "Faturas e plano elite",
    settingLanguage = "Selecione o seu Idioma",
    settingLanguageSubtitle = "Português (PT)",

    // User Profile
    userRoleBadge = "ATLETA ELITE",

    // Admin Profile
    adminRoleBadge = "ADMIN",

    // Organizador Profile
    organizadorRoleBadge = "ORGANIZADOR",
    sectionAccountSettings = "CONFIGURAÇÕES DE CONTA",
    sectionSecuritySupport = "SEGURANÇA E SUPORTE",
    settingEditProfile = "Editar Perfil",
    settingEditProfileSubtitle = "Alterar nome, foto e bio",
    settingOrgData = "Dados da Organização",
    settingOrgDataSubtitle = "NIF, Morada e Contactos",
    settingNotificationsOrgSubtitle = "Alertas de inscrições e pagamentos",
    settingSecurity = "Segurança",
    settingSecuritySubtitle = "Password e 2FA",
    settingHelpCenter = "Centro de Ajuda",
    settingHelpCenterSubtitle = "Falar com o suporte",
    metricActiveTournaments = "TORNEIOS ATIVOS",
    metricRegisteredAthletes = "ATLETAS INSCRITOS",

    // UserHomeScreen
    liveEventsTitle = "Eventos a Decorrer",
    upcomingEventsTitle = "Próximos Eventos",
    promoBannerLine1 = "O SEU",
    promoBannerLine2 = "DESEMPENHO É A",
    promoBannerLine3 = "NOSSA META.",

    // UserEventsScreen
    eventsPageLabel = "CALENDÁRIO NACIONAL",
    eventsPageTitle1 = "Próximos ",
    eventsPageTitle2 = "Eventos",
    eventsPageSubtitle = "Explora os torneios e competições de elite. Garante o teu lugar nas maiores arenas desportivas do país.",
    searchPlaceholder = "Procurar torneios ou modalidades...",
    spotsLeft = "VAGAS RESTANTES",
    soldOut = "INSCRIÇÕES ESGOTADAS",
    registerNow = "INSCREVER AGORA",
    unavailable = "INDISPONÍVEL",

    // UserSubscriptionsScreen
    subsTitle = "Minhas Inscrições",
    subsSubtitle = "Gere as tuas participações nos próximos torneios de elite.",
    filterAll = "TODAS",
    filterActive = "ATIVAS",
    filterCompleted = "CONCLUÍDAS",
    statusConfirmed = "CONFIRMADO",
    statusPending = "PENDENTE",
    statusFinished = "CONCLUÍDO",
    btnViewDetails = "VER DETALHES",
    btnCompletePayment = "FINALIZAR PAGAMENTO",
    btnWithdraw = "DESISTIR",

    // AdminDashboard
    searchTooltip = "Pesquisar",

    // Language Picker
    languageDialogTitle = "Idioma da App",
    languageDialogSubtitle = "Escolhe o idioma para toda a aplicação",
    languagePT = "Português (PT)",
    languageEN = "English (EN)",
)

val EnglishStrings = AppStrings(
    // Navigation
    navHome = "HOME",
    navEvents = "EVENTS",
    navSubscriptions = "ENTRIES",
    navProfile = "PROFILE",
    navStats = "STATISTICS",
    navCreateEvent = "CREATE EVENT",

    // Common
    signOut = "Sign Out",
    search = "Search",
    filters = "FILTERS",
    viewAll = "View all",
    filter = "Filter",
    calendar = "Calendar",
    open = "Open",
    editPhoto = "Edit Photo",

    // Profile shared
    sectionAccountData = "ACCOUNT DATA",
    sectionSettings = "SETTINGS",
    fieldEmail = "Email",
    fieldPhone = "Phone",
    fieldLocation = "Location",
    settingNotifications = "Notifications",
    settingNotificationsSubtitle = "Alert preferences",
    settingPrivacy = "Privacy",
    settingPrivacySubtitle = "Visibility control",
    settingChangePassword = "Change Password",
    settingChangePasswordSubtitle = "Update credentials",
    settingSubscriptionPayments = "Subscription & Payments",
    settingSubscriptionPaymentsSubtitle = "Invoices and elite plan",
    settingLanguage = "Select your Language",
    settingLanguageSubtitle = "English (EN)",

    // User Profile
    userRoleBadge = "ELITE ATHLETE",

    // Admin Profile
    adminRoleBadge = "ADMIN",

    // Organizador Profile
    organizadorRoleBadge = "ORGANIZER",
    sectionAccountSettings = "ACCOUNT SETTINGS",
    sectionSecuritySupport = "SECURITY & SUPPORT",
    settingEditProfile = "Edit Profile",
    settingEditProfileSubtitle = "Change name, photo and bio",
    settingOrgData = "Organization Data",
    settingOrgDataSubtitle = "VAT, Address and Contacts",
    settingNotificationsOrgSubtitle = "Registration and payment alerts",
    settingSecurity = "Security",
    settingSecuritySubtitle = "Password and 2FA",
    settingHelpCenter = "Help Center",
    settingHelpCenterSubtitle = "Talk to support",
    metricActiveTournaments = "ACTIVE TOURNAMENTS",
    metricRegisteredAthletes = "REGISTERED ATHLETES",

    // UserHomeScreen
    liveEventsTitle = "Live Events",
    upcomingEventsTitle = "Upcoming Events",
    promoBannerLine1 = "YOUR",
    promoBannerLine2 = "PERFORMANCE IS",
    promoBannerLine3 = "OUR GOAL.",

    // UserEventsScreen
    eventsPageLabel = "NATIONAL CALENDAR",
    eventsPageTitle1 = "Upcoming ",
    eventsPageTitle2 = "Events",
    eventsPageSubtitle = "Explore elite tournaments and competitions. Secure your spot in the biggest sports arenas in the country.",
    searchPlaceholder = "Search tournaments or sports...",
    spotsLeft = "SPOTS LEFT",
    soldOut = "SOLD OUT",
    registerNow = "REGISTER NOW",
    unavailable = "UNAVAILABLE",

    // UserSubscriptionsScreen
    subsTitle = "My Entries",
    subsSubtitle = "Manage your participations in upcoming elite tournaments.",
    filterAll = "ALL",
    filterActive = "ACTIVE",
    filterCompleted = "COMPLETED",
    statusConfirmed = "CONFIRMED",
    statusPending = "PENDING",
    statusFinished = "FINISHED",
    btnViewDetails = "VIEW DETAILS",
    btnCompletePayment = "COMPLETE PAYMENT",
    btnWithdraw = "WITHDRAW",

    // AdminDashboard
    searchTooltip = "Search",

    // Language Picker
    languageDialogTitle = "App Language",
    languageDialogSubtitle = "Choose the language for the entire app",
    languagePT = "Português (PT)",
    languageEN = "English (EN)",
)
