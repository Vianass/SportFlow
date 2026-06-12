package com.sportflow.app.ui.localization

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.sportflow.app.model.AppLanguage
import java.util.Locale

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.PT }

@Composable
fun localizedText(portuguese: String, english: String? = null): String {
    return english?.takeIf { LocalAppLanguage.current == AppLanguage.EN }
        ?: translateText(portuguese, LocalAppLanguage.current)
}

internal fun translateText(source: String, language: AppLanguage): String =
    if (language == AppLanguage.PT) source else PortugueseToEnglish.translate(source)

@Composable
fun appLocale(): Locale = if (LocalAppLanguage.current == AppLanguage.PT) {
    Locale.forLanguageTag("pt-PT")
} else {
    Locale.ENGLISH
}

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    MaterialText(
        text = localizedText(text),
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
    )
}

@Composable
fun Text(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    MaterialText(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        inlineContent = inlineContent,
        onTextLayout = onTextLayout,
        style = style,
    )
}

private object PortugueseToEnglish {
    private val exact = mapOf(
        "A CARREGAR ECOSSISTEMA..." to "LOADING ECOSYSTEM...",
        "A PROCESSAR..." to "PROCESSING...",
        "PERFORMANCE EM TEMPO REAL" to "REAL-TIME PERFORMANCE",
        "A plataforma de gestão desportiva definitiva para clubes, organizadores de torneios e atletas de alto rendimento." to
            "The definitive sports management platform for clubs, tournament organizers and high-performance athletes.",
        "A plataforma definitiva para gerir, organizar e acompanhar os teus torneios desportivos em tempo real." to
            "The definitive platform to manage, organize and follow your sports tournaments in real time.",
        "ESTÁ NA HORA DE JOGAR" to "IT'S TIME TO PLAY",
        "CRIA E GERE\nO TEU" to "CREATE AND MANAGE\nYOUR",
        "COMEÇAR AGORA" to "GET STARTED",
        "VER DOCUMENTAÇÃO" to "VIEW DOCUMENTATION",
        "Política de Priv." to "Privacy Policy",
        "Todos os direitos reservados." to "All rights reserved.",
        "© 2024 SportFlow. Todos os direitos reservados." to "© 2024 SportFlow. All rights reserved.",
        "Bem-vindo de\nvolta" to "Welcome\nback",
        "Escolha o seu perfil e aceda à sua conta" to "Choose your profile and access your account",
        "TIPO DE UTILIZADOR" to "USER TYPE",
        "PALAVRA-PASSE" to "PASSWORD",
        "CONFIRMAR PALAVRA-PASSE" to "CONFIRM PASSWORD",
        "Esqueceu-se da palavra-passe?" to "Forgot your password?",
        "Por favor, preencha todos os campos" to "Please fill in all fields",
        "ENTRAR NA ARENA" to "ENTER THE ARENA",
        "OU CONTINUE COM" to "OR CONTINUE WITH",
        "Não tem uma conta? " to "Don't have an account? ",
        "Criar conta grátis" to "Create a free account",
        "Criar a sua\nconta" to "Create your\naccount",
        "Registe-se e comece a gerir o seu torneio" to "Sign up and start managing your tournament",
        "NOME COMPLETO" to "FULL NAME",
        "O seu nome completo" to "Your full name",
        "Mínimo 6 caracteres" to "Minimum 6 characters",
        "As palavra-passes não coincidem" to "Passwords do not match",
        "REGISTAR NA ARENA" to "REGISTER IN THE ARENA",
        "Já tem uma conta? " to "Already have an account? ",
        "Iniciar Sessão" to "Sign In",
        "INÍCIO" to "HOME",
        "EVENTOS" to "EVENTS",
        "INSCRIÇÕES" to "ENTRIES",
        "PERFIL" to "PROFILE",
        "ESTATÍSTICAS" to "STATISTICS",
        "CRIAR EVENTO" to "CREATE EVENT",
        "Terminar Sessão" to "Sign Out",
        "Pesquisar" to "Search",
        "Pesquisar por nome, modalidade ou tipo..." to "Search by name, sport or type...",
        "Procurar torneios ou modalidades..." to "Search tournaments or sports...",
        "Mais opções" to "More options",
        "Filtros" to "Filters",
        "FILTROS" to "FILTERS",
        "Filtrar" to "Filter",
        "Calendário" to "Calendar",
        "CALENDÁRIO" to "CALENDAR",
        "LIMPAR" to "CLEAR",
        "APLICAR" to "APPLY",
        "Fechar" to "Close",
        "FECHAR" to "CLOSE",
        "Voltar" to "Back",
        "Remover" to "Remove",
        "Editar" to "Edit",
        "Sair" to "Sign out",
        "Abrir" to "Open",
        "Detalhes" to "Details",
        "Mostrar" to "Show",
        "Ocultar" to "Hide",
        "Success" to "Success",
        "Mês Anterior" to "Previous month",
        "Próximo Mês" to "Next month",
        "Modalidade" to "Sport",
        "Disponibilidade" to "Availability",
        "Todas" to "All",
        "Com vagas" to "Available spots",
        "CONFIGURAÇÕES" to "SETTINGS",
        "DADOS DA CONTA" to "ACCOUNT DATA",
        "CONFIGURAÇÕES DE CONTA" to "ACCOUNT SETTINGS",
        "SEGURANÇA E SUPORTE" to "SECURITY & SUPPORT",
        "Notificações" to "Notifications",
        "Preferências de alerta" to "Alert preferences",
        "Ativadas" to "Enabled",
        "Desativadas" to "Disabled",
        "Privacidade" to "Privacy",
        "Controlo de visibilidade" to "Visibility control",
        "Segurança" to "Security",
        "Alterar Palavra-passe" to "Change Password",
        "Atualizar credenciais" to "Update credentials",
        "Métodos de Pagamento" to "Payment Methods",
        "Adicionar cartão ou MB Way" to "Add card or MB Way",
        "Selecione o seu Idioma" to "Select your Language",
        "Idioma da App" to "App Language",
        "Escolhe o idioma para toda a aplicação" to "Choose the language for the entire app",
        "Português" to "Portuguese",
        "Editar Foto" to "Edit Photo",
        "Foto de Perfil" to "Profile photo",
        "Mudar foto" to "Change photo",
        "Editar Perfil" to "Edit Profile",
        "Alterar nome, foto e bio" to "Change name, photo and bio",
        "Nome Completo" to "Full Name",
        "Email Público" to "Public Email",
        "Telemóvel" to "Phone",
        "Localização" to "Location",
        "GUARDAR" to "SAVE",
        "GUARDAR ALTERAÇÕES" to "SAVE CHANGES",
        "CANCELAR" to "CANCEL",
        "Dados da Organização" to "Organization Details",
        "NIF, Morada e Contactos" to "VAT, Address and Contacts",
        "Nome da Organização" to "Organization Name",
        "Morada (Sede)" to "Address (Head Office)",
        "Contacto Principal" to "Main Contact",
        "Atualize as informações legais e de contacto da sua organização." to
            "Update your organization's legal and contact information.",
        "Centro de Ajuda" to "Help Center",
        "Falar com o suporte" to "Contact support",
        "A nossa equipa de suporte está pronta para ajudar. Descreva o seu problema abaixo." to
            "Our support team is ready to help. Describe your issue below.",
        "Assunto" to "Subject",
        "Mensagem" to "Message",
        "Mensagem enviada com sucesso! A nossa equipa entrará em contacto em breve." to
            "Message sent successfully! Our team will contact you shortly.",
        "ENVIAR" to "SEND",
        "Método de Pagamento" to "Payment Method",
        "Cartão de Crédito" to "Credit Card",
        "Rápido e seguro pelo telemóvel" to "Fast and secure on your phone",
        "Número de Telemóvel" to "Phone Number",
        "Número do Cartão" to "Card Number",
        "A validar o teu método" to "Validating your method",
        "O método foi guardado!" to "Method was saved!",
        "Este método será usado na tua próxima inscrição." to "This method will be used for your next entry.",
        "Tudo pronto!" to "All set!",
        "Pagamento concluído com sucesso!" to "Payment completed successfully!",
        "Gere as tuas preferências de alerta" to "Manage your alert preferences",
        "Recebe alertas de novos eventos e atualizações" to "Receive alerts about new events and updates",
        "Não receberes nenhuma notificação da app" to "You won't receive any app notifications",
        "Perfil Público" to "Public Profile",
        "O teu nome aparece nas classificações públicas" to "Your name appears in public rankings",
        "Permitir que a app use a tua localização" to "Allow the app to use your location",
        "Atletas inscritos nos teus eventos podem ver o teu email e telemóvel" to
            "Athletes registered for your events can see your email and phone number",
        "Inscrição Confirmada" to "Entry Confirmed",
        "O teu lugar está garantido neste torneio. Prepara-te para a competição!" to
            "Your place in this tournament is secured. Get ready to compete!",
        "Torneio" to "Tournament",
        "Categoria" to "Category",
        "Data" to "Date",
        "Local" to "Venue",
        "Pagamento" to "Payment",
        "DESCARREGAR FATURA" to "DOWNLOAD INVOICE",
        "Inscrever no Torneio" to "Enter Tournament",
        "Revê os detalhes antes de garantires a vaga da tua equipa. As vagas são limitadas!" to
            "Review the details before securing your team's place. Spots are limited!",
        "Valor da Inscrição" to "Entry Fee",
        "AVANÇAR PARA PAGAMENTO" to "CONTINUE TO PAYMENT",
        "A DECORRER" to "LIVE",
        "Estatísticas do Jogo" to "Match Statistics",
        "Classificação em Tempo Real" to "Live Standings",
        "Minhas Inscrições" to "My Entries",
        "Gere as tuas participações nos próximos torneios de elite." to
            "Manage your participation in upcoming elite tournaments.",
        "TODAS" to "ALL",
        "ATIVAS" to "ACTIVE",
        "CONCLUÍDAS" to "COMPLETED",
        "CONFIRMADO" to "CONFIRMED",
        "PENDENTE" to "PENDING",
        "CONCLUÍDO" to "FINISHED",
        "VER DETALHES" to "VIEW DETAILS",
        "FINALIZAR PAGAMENTO" to "COMPLETE PAYMENT",
        "DESISTIR" to "WITHDRAW",
        "A aguardar confirmação de pagamento via MBWay." to "Awaiting MB Way payment confirmation.",
        "4º Classificado" to "4th Place",
        "CALENDÁRIO NACIONAL" to "NATIONAL CALENDAR",
        "Próximos " to "Upcoming ",
        "Próximos Eventos" to "Upcoming Events",
        "Eventos a Decorrer" to "Live Events",
        "Explora os torneios e competições de elite. Garante o teu lugar nas maiores arenas desportivas do país." to
            "Explore elite tournaments and competitions. Secure your place in the country's biggest sports arenas.",
        "VAGAS RESTANTES" to "SPOTS LEFT",
        "INSCRIÇÕES ESGOTADAS" to "SOLD OUT",
        "INSCREVER AGORA" to "REGISTER NOW",
        "INDISPONÍVEL" to "UNAVAILABLE",
        "INSCRIÇÃO ABERTA" to "REGISTRATION OPEN",
        "DESEMPENHO É A" to "PERFORMANCE IS",
        "NOSSA META." to "OUR GOAL.",
        "VISÃO GERAL DO SISTEMA" to "SYSTEM OVERVIEW",
        "Painel de " to "Control ",
        "Controlo" to "Panel",
        "Bem-vindo novamente, Diretor. Tem 4 torneios ativos e 12 jogos agendados para hoje." to
            "Welcome back, Director. You have 4 active tournaments and 12 matches scheduled for today.",
        "Novo Evento" to "New Event",
        "Editar Evento" to "Edit Event",
        "Torneios Ativos" to "Active Tournaments",
        "Ver Todos" to "View All",
        "ATLETAS" to "ATHLETES",
        "RECEITA" to "REVENUE",
        "DESEMPENHO" to "PERFORMANCE",
        "+24% Este Mês" to "+24% This Month",
        "Próximos Jogos" to "Upcoming Matches",
        "VER HISTÓRICO" to "VIEW HISTORY",
        "HOJE" to "TODAY",
        "AMANHÃ" to "TOMORROW",
        "CASA" to "HOME",
        "FORA" to "AWAY",
        "Nova Inscrição Recebida" to "New Entry Received",
        "A equipa 'Alpha Lions' confirmou a participação no Open." to
            "The 'Alpha Lions' team confirmed its participation in the Open.",
        "Relatório Semanal" to "Weekly Report",
        "O resumo financeiro da semana já se encontra disponível." to
            "The weekly financial summary is now available.",
        "Há 5 min" to "5 min ago",
        "Há 22 min" to "22 min ago",
        "Há 2 horas" to "2 hours ago",
        "Eventos Criados" to "Created Events",
        "Crie, edite e organize competições." to "Create, edit and organize competitions.",
        "Estatísticas Gerais" to "General Statistics",
        "Faturação, número de atletas e relatórios avançados." to
            "Revenue, athlete numbers and advanced reports.",
        "Perfil do Diretor" to "Director Profile",
        "Configurações da conta administrativa." to "Administrative account settings.",
        "GESTÃO DE COMPETIÇÕES" to "COMPETITION MANAGEMENT",
        "Gira os seus campeonatos, acompanhe resultados e organize chaves de forma profissional." to
            "Manage your championships, track results and organize brackets professionally.",
        "NOVA COMPETIÇÃO" to "NEW COMPETITION",
        "INSCRIÇÕES ABERTAS" to "REGISTRATION OPEN",
        "ELIMINATÓRIAS" to "KNOCKOUTS",
        "Eliminatórias" to "Knockouts",
        "CONCLUÍDOS" to "COMPLETED",
        "CONTINUAR CONFIGURAÇÃO" to "CONTINUE SETUP",
        "CALENDÁRIO DE JOGOS" to "MATCH CALENDAR",
        "VER CALENDÁRIO COMPLETO" to "VIEW FULL CALENDAR",
        "VER CHAVES →" to "VIEW BRACKETS →",
        "RELATÓRIOS" to "REPORTS",
        "Configurações da conta de organização." to "Organization account settings.",
        "Inscrições Recebidas" to "Entries Received",
        "Monitorize as inscrições efetuadas nos seus torneios." to "Monitor entries submitted to your tournaments.",
        "Gestão de\nInscrições" to "Entry\nManagement",
        "Seleciona um evento para gerir equipas e inscrições." to "Select an event to manage teams and entries.",
        "EQUIPAS E ELENCOS" to "TEAMS AND ROSTERS",
        "EQUIPAS CONFIRMADAS" to "CONFIRMED TEAMS",
        "JOGADORES INSCRITOS" to "REGISTERED PLAYERS",
        "NOVA EQUIPA" to "NEW TEAM",
        "Capitão: Bernardo Fernandes" to "Captain: Bernardo Fernandes",
        "VALIDADO" to "VALIDATED",
        "INCOMPLETO" to "INCOMPLETE",
        "A aguardar confirmação (4/7)" to "Awaiting confirmation (4/7)",
        "+ ASSOCIAR JOGADOR" to "+ LINK PLAYER",
        "Registo de Partida" to "Match Record",
        "100 pés" to "100 feet",
        "14 Golos" to "14 Goals",
        "9 Golos" to "9 Goals",
        "CONFIRMAR AGENDAMENTO" to "CONFIRM SCHEDULE",
        "Gerir Evento  →" to "Manage Event  →",
        "Gerir Planeamento  ⚙" to "Manage Schedule  ⚙",
        "Ver Relatório  📄" to "View Report  📄",
        "Ao publicar, o evento fica visível para inscrições no ecossistema Elite Arena." to
            "Once published, the event becomes available for registration in the Elite Arena ecosystem.",
        "NÍVEL DE COMPETIÇÃO" to "COMPETITION LEVEL",
        "Gestão de Calendário" to "Schedule Management",
        "Descreva as regras específicas, formato de pontuação e conduta..." to
            "Describe the specific rules, scoring format and conduct...",
        "Fase de grupos + eliminatórias" to "Group stage + knockouts",
        "K.O. direto até à final" to "Single elimination through to the final",
        "DISPONÍVEL" to "AVAILABLE",
        "EVOLUÇÃO DE PERFORMANCE" to "PERFORMANCE TREND",
        "Estatísticas" to "Statistics",
        "VITÓRIAS" to "WINS",
        "Top 5% de 1.240 Jogadores" to "Top 5% of 1,240 Players",
        "Atualiza as tuas credenciais de acesso" to "Update your access credentials",
        "Apenas com vagas" to "Only with available spots",
        "Filtros ativos:" to "Active filters:",
        "Limpar Todos os Filtros" to "Clear All Filters",
        "Tente alterar os filtros selecionados ou limpe os filtros ativos." to
            "Try changing the selected filters or clear the active filters.",
        "Futebol" to "Football",
        "FUTEBOL" to "FOOTBALL",
        "Ténis" to "Tennis",
        "TÉNIS" to "TENNIS",
        "Equipa A" to "Team A",
        "Equipa B" to "Team B",
        "Jogador 1" to "Player 1",
        "Jogador 2" to "Player 2",
        "Posse de Bola" to "Possession",
        "Progresso do Jogo" to "Match Progress",
        "CLIQUE DUPLO PARA PAGAR" to "DOUBLE TAP TO PAY",
        "Confirma com Face ID ou Touch ID" to "Confirm with Face ID or Touch ID",
        "Paga com um toque" to "Pay with one tap",
        "Controla a tua visibilidade na app" to "Control your visibility in the app",
        "Outros atletas podem ver o teu perfil" to "Other athletes can see your profile",
        "Visibilidade dos teus dados de contacto" to "Visibility of your contact details",
        "Open Masculino (Nível 3)" to "Men's Open (Level 3)",
        "25,00€ / equipa" to "€25.00 / team",
        "45,00€ / equipa" to "€45.00 / team",
        "MM/AA" to "MM/YY",
        "CRIA E GERE O TEU TORNEIO" to "CREATE AND MANAGE YOUR TOURNAMENT",
        "TORNEIO" to "TOURNAMENT",
        "Central de Ajuda" to "Help Center",
        "Termos de Uso" to "Terms of Use",
        "+ CRIAR TORNEIO" to "+ CREATE TOURNAMENT",
        "CRIAR TORNEIO" to "CREATE TOURNAMENT",
        "NOME DO EVENTO" to "EVENT NAME",
        "A maior competição regional de futebol society, reunindo as 16 melhores equipas..." to
            "The largest regional small-sided football competition, bringing together the top 16 teams...",
        "Circuito de verão com premiação recorde em 2024." to "Summer circuit with record prize money in 2024.",
        "Ex: Torneio de Primavera" to "E.g.: Spring Tournament",
        "Quartos-de-Final #3" to "Quarter-final #3",
        "Torneio 3x3 modalidade livre para atletas federados." to
            "Open-format 3x3 tournament for registered athletes.",
        "16 Equipas • Quartos-de-final" to "16 Teams • Quarter-finals",
        "8 Equipas • Fase de Grupos" to "8 Teams • Group Stage",
        "Alerta de Arbitragem" to "Referee Alert",
        "Jogo das 16:00 ainda sem árbitro principal atribuído." to
            "The 16:00 match still has no head referee assigned.",
        "Opções" to "Options",
        "Desempenho da Temporada 2024" to "2024 Season Performance",
        "DA LIGA" to "IN THE LEAGUE",
        "TOP MARCADORES DA LIGA" to "LEAGUE TOP SCORERS",
        "João Silva (Tu)" to "João Silva (You)",
        "Criar Novo Evento" to "Create New Event",
        "Detalhes do Torneio" to "Tournament Details",
        "Defina as datas das jornadas e fases finais." to "Set the dates for matchdays and final stages.",
        "Dica: O formato de eliminatórias é ideal para torneios rápidos de um fim de semana." to
            "Tip: The knockout format is ideal for quick weekend tournaments.",
        "Ex: Torneio ESTG 2026" to "E.g.: ESTG Tournament 2026",
        "Publicar Evento" to "Publish Event",
        "Perfil do Organizador" to "Organizer Profile",
        "Gestão de" to "Management of",
        "Torneio: ESTG Futsal 2026" to "Tournament: ESTG Futsal 2026",
        "Torneio: ESTG\nFutsal 2026" to "Tournament: ESTG\nFutsal 2026",
        "Visibilidade de dados" to "Data visibility",
        "Nenhum torneio encontrado" to "No tournaments found",
        "Tente alterar os termos da pesquisa ou limpe os filtros ativos." to
            "Try changing your search terms or clear the active filters.",
        "FUTEBOL 7 • SÉRIE B" to "7-A-SIDE FOOTBALL • SERIES B",
        "TÉNIS • SINGULARES" to "TENNIS • SINGLES",
        " • INSCRIÇÃO ABERTA" to " • REGISTRATION OPEN",
        "Nenhum evento encontrado" to "No events found",
        "Chamada de Atletas" to "Athlete Call",
        "O SEU" to "YOUR",
        "100m Barreiras - Final" to "100m Hurdles - Final",
        "MEETING DE ATLETISMO" to "ATHLETICS MEET",
        "Ténis Amador" to "Amateur Tennis",
        "Organizador de torneios de futsal da ESTG." to "ESTG futsal tournament organizer.",
        "Organizador de torneios de futsal na ESTG." to "Futsal tournament organizer at ESTG.",
    )

    private val monthNames = listOf(
        "janeiro" to "January", "fevereiro" to "February", "março" to "March",
        "abril" to "April", "maio" to "May", "junho" to "June",
        "julho" to "July", "agosto" to "August", "setembro" to "September",
        "outubro" to "October", "novembro" to "November", "dezembro" to "December",
        "jan" to "Jan", "fev" to "Feb", "mar" to "Mar", "abr" to "Apr",
        "mai" to "May", "jun" to "Jun", "jul" to "Jul", "ago" to "Aug",
        "set" to "Sep", "out" to "Oct", "nov" to "Nov", "dez" to "Dec",
    )

    fun translate(source: String): String {
        exact[source]?.let { return it }

        Regex("Restam apenas (\\d+) vagas! Inscreve-te rápido\\.").matchEntire(source)?.let {
            return "Only ${it.groupValues[1]} spots left! Register now."
        }
        Regex("(\\d+) VAGAS RESTANTES").matchEntire(source)?.let {
            return "${it.groupValues[1]} SPOTS LEFT"
        }
        Regex("INICIA EM (\\d+)H").matchEntire(source)?.let {
            return "STARTS IN ${it.groupValues[1]}H"
        }
        Regex("(\\d{1,2}) de ([^,]+), (.+)").matchEntire(source)?.let {
            return "${it.groupValues[1]} ${translate(it.groupValues[2])}, ${it.groupValues[3]}"
        }
        Regex("(\\d+)º").matchEntire(source)?.let {
            val position = it.groupValues[1].toIntOrNull() ?: return source
            val suffix = when {
                position % 100 in 11..13 -> "th"
                position % 10 == 1 -> "st"
                position % 10 == 2 -> "nd"
                position % 10 == 3 -> "rd"
                else -> "th"
            }
            return "$position$suffix"
        }

        var translated = source
        monthNames.forEach { (pt, en) ->
            translated = translated.replace(Regex("(?i)\\b${Regex.escape(pt)}\\b")) { match ->
                if (match.value.all(Char::isUpperCase)) en.uppercase() else en
            }
        }
        return translated
    }
}
