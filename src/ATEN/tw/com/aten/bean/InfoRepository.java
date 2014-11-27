// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package tw.com.aten.bean;

import java.util.Hashtable;

// Referenced classes of package tw.com.aten.bean:
//            ConnInfo, PlatformInfo, UserInfo, VirtualUsbInfo

public class InfoRepository
{
    public static class UILanguage
    {

        public static String ENUIString[] = {
            "Exit", "Yes", "No", "Are you sure?", "Virtual Device", "Record", "Playback", "Macro", "Options", "Scale", 
            "User List", "Hotkey Settings", "action", "Hotkeys", "Keyboard Monitor", "Start", "Stop", "Assign", "Close", "Input", 
            "Video", "Toggle Event", "Mouse Mode", "Keyboard Mode", "Repeat Key Timeout", "Image Scale", "Image Quality", "Low", "High", "Fit to Window", 
            "Move OSD", "Settings for Device1", "Settings for Device2", "Logical Drive Type", "Connect", "Plug in", "Unplug", "Connection Status History", "Open Image", "Image File Name and Full Path", 
            "Language Setting", "Refresh", "OK", "Open", "Play/Pause", "Preference", "Keyboard Mouse Hotplug", "Show User List", "minute(s)", "Cancel", 
            "Session ID", "User Name", "Hold down", "Press and Release", "disconnnect due to web logout", "Mouse Settings", "Absolute Mouse (Windows)", "Relative Mouse (Linux)", "Enable Mouse Input", "Keyboard Settings", 
            "Enable Keyboard Input", "Display", "Performance", "Enable FPS Control", "frame per second (FPS)", "Recording Time", "Enable auto stop after ", "Display Scale", "Virtual Storage", "Virtual Media", 
            "USB Floppy&Flash", "CDROM&ISO", "Plug Out", "IP Address", "Adjust Mouse", "Exit Remote Location", "Full-Screen Mode", "Refresh Window", "Send Ctrl+Alt+Del", "Toggle Mouse Display", 
            "Toggle UI Display", "Hold Right Alt Key", "Hold Left Alt Key", "Right Window Key", "Left Window Key", "Space", "Enter", "Hyphen", "OSD UI Style", "Menubar UI Style", 
            "Leave Full Screen Mode", "Virtual Keyboard", "Normal Mode", "Enhanced Text Mode", "Network Issue", "Connection failed", "Session issue", "The maximum session number has been reached", "EOF(End Of File)", "Refresh screen", 
            "Start recording", "Stop recording", "English keyboard", "Chinese(traditional) keyboard", "Japanese keyboard", "German keyboard", "French keyboard", "Spanish keyboard", "Korean keyboard", "Italian keyboard", 
            "Capture", "Full screen view", "Auto-resize window", "Settings for Device3", "Device1", "Device2", "Device3", "United Kingdom keyboard"
        };
        public static String JPUIString[] = {
            "Exit", "\u306F\u3044", "\u3044\u3044\u3048", "\u672C\u5F53\u306B\u3088\u308D\u3057\u3044\u3067\u3059\u304B\uFF1F", "\u30D0\u30FC\u30C1\u30E3\u30EB\u30C7\u30D0\u30A4\u30B9", "\u8A18\u9332", "\u30D7\u30EC\u30A4\u30D0\u30C3\u30AF", "\u30DE\u30AF\u30ED", "\u30AA\u30D7\u30B7\u30E7\u30F3", "\u30B9\u30B1\u30FC\u30EB", 
            "\u30E6\u30FC\u30B6\u30FC\u30EA\u30B9\u30C8", "\u30DB\u30C3\u30C8\u30AD\u30FC\u8A2D\u5B9A", "\u30A2\u30AF\u30B7\u30E7\u30F3", "\u30DB\u30C3\u30C8\u30AD\u30FC", "\u30AD\u30FC\u30DC\u30FC\u30C9\u30E2\u30CB\u30BF\u30FC", "Start", "\u505C\u6B62", "\u30A2\u30B5\u30A4\u30F3", "\u9589\u3058\u308B", "\u5165\u529B", 
            "\u30D3\u30C7\u30AA", "\u30A4\u30D9\u30F3\u30C8\u4EA4\u63DB", "\u30DE\u30A6\u30B9\u30E2\u30FC\u30C9", "\u30AD\u30FC\u30DC\u30FC\u30C9\u30E2\u30FC\u30C9", "\u30EA\u30D4\u30FC\u30C8\u30AD\u30FC\u30BF\u30A4\u30E0\u30A2\u30A6\u30C8", "\u30A4\u30E1\u30FC\u30B8\u30B9\u30B1\u30FC\u30EB", "\u30A4\u30E1\u30FC\u30B8\u54C1\u8CEA", "Low", "High", "\u30A6\u30A3\u30F3\u30C9\u30A6\u306B\u5408\u308F\u305B\u308B", 
            "OSD\u306E\u79FB\u52D5", "\u30C7\u30D0\u30A4\u30B9\u8A2D\u5B9A1", "\u30C7\u30D0\u30A4\u30B9\u8A2D\u5B9A2", "\u8AD6\u7406\u30C9\u30E9\u30A4\u30D6\u30BF\u30A4\u30D7", "\u63A5\u7D9A", "Plug in", "Unplug", "\u63A5\u7D9A\u30B9\u30C6\u30FC\u30BF\u30B9\u5C65\u6B74", "\u30A4\u30E1\u30FC\u30B8\u3092\u958B\u304F", "\u30A4\u30E1\u30FC\u30B8\u30D5\u30A1\u30A4\u30EB\u540D\u3068\u30D5\u30EB\u30D1\u30B9", 
            "\u8A00\u8A9E\u8A2D\u5B9A", "\u518D\u8AAD\u8FBC", "OK", "\u30AA\u30FC\u30D7\u30F3", "\u518D\u751F/\u4E00\u6642\u505C\u6B62", "\u304A\u6C17\u306B\u5165\u308A", "\u30AD\u30FC\u30DC\u30FC\u30C9/\u30DE\u30A6\u30B9 \u30DB\u30C3\u30C8\u30D7\u30E9\u30B0", "\u30E6\u30FC\u30B6\u30FC\u4E00\u89A7\u8868\u793A", "\u5206", "\u30AD\u30E3\u30F3\u30BB\u30EB", 
            "\u30BB\u30C3\u30B7\u30E7\u30F3ID", "\u30E6\u30FC\u30B6\u30FC\u30CD\u30FC\u30E0", "\u9577\u62BC\u3057\u3059\u308B", "\u62BC\u3057\u3066\u96E2\u3059", "\u30ED\u30B0\u30A2\u30A6\u30C8\u306E\u305F\u3081\u306B\u5207\u65AD\u3057\u307E\u3059", "\u30DE\u30A6\u30B9\u8A2D\u5B9A", "\u7D76\u5BFE\u5EA7\u6A19\u30DE\u30A6\u30B9\uFF08Windows\uFF09", "\u76F8\u5BFE\u5EA7\u6A19\u30DE\u30A6\u30B9\uFF08Linux\uFF09", "\u30DE\u30A6\u30B9\u5165\u529B\u3092\u6709\u52B9\u306B\u3059\u308B", "\u30AD\u30FC\u30DC\u30FC\u30C9\u8A2D\u5B9A", 
            "\u30AD\u30FC\u30DC\u30FC\u30C9\u5165\u529B\u3092\u6709\u52B9\u306B\u3059\u308B", "\u30C7\u30A3\u30B9\u30D7\u30EC\u30A4", "\u30D1\u30D5\u30A9\u30FC\u30DE\u30F3\u30B9", "FPS\u5236\u5FA1\u3092\u6709\u52B9\u306B\u3059\u308B", "FPS", "\u30EC\u30B3\u30FC\u30C7\u30A3\u30F3\u30B0\u6642\u9593", "\u81EA\u52D5\u505C\u6B62\u3092\u6709\u52B9\u306B\u3059\u308B", "\u30C7\u30A3\u30B9\u30D7\u30EC\u30A4\u30B9\u30B1\u30FC\u30EB", "\u4EEE\u60F3\u30B9\u30C8\u30EC\u30FC\u30B8", "\u4EEE\u60F3\u30E1\u30C7\u30A3\u30A2", 
            "USB\u30D5\u30ED\u30C3\u30D4\u30FC\uFF06Flash", "CD-ROM\uFF06ISO", "Plug Out", "IP\u30A2\u30C9\u30EC\u30B9", "\u30DE\u30A6\u30B9\u540C\u671F", "\u30EA\u30E2\u30FC\u30C8\u30ED\u30B1\u30FC\u30B7\u30E7\u30F3\u304B\u3089\u9000\u51FA", "\u30D5\u30EB\u30B9\u30AF\u30EA\u30FC\u30F3\u30E2\u30FC\u30C9", "Refresh Window", "Ctrl+Alt+Del\u3092\u9001\u4FE1", "\u30DE\u30A6\u30B9\u8868\u793A\u3092\u5207\u308A\u66FF\u3048\u308B", 
            "UI\u30B9\u30BF\u30A4\u30EB\u5207\u66FF", "\u53F3Alt\u30AD\u30FC\u3092\u30DB\u30FC\u30EB\u30C9\u3059\u308B", "\u5DE6Alt\u30AD\u30FC\u3092\u30DB\u30FC\u30EB\u30C9\u3059\u308B", "\u53F3Windows\u30AD\u30FC", "\u5DE6Windows\u30AD\u30FC", "\u30B9\u30DA\u30FC\u30B9", "Enter", "\u30CF\u30A4\u30D5\u30F3", "OSD UI\u30B9\u30BF\u30A4\u30EB", "\u30E1\u30CB\u30E5\u30FC\u30D0\u30FCUI\u30B9\u30BF\u30A4\u30EB", 
            "\u30D5\u30EB\u30B9\u30AF\u30EA\u30FC\u30F3\u30E2\u30FC\u30C9\u3092\u9000\u51FA", "\u30D0\u30FC\u30C1\u30E3\u30EB\u30AD\u30FC\u30DC\u30FC\u30C9", "\u30CE\u30FC\u30DE\u30EB\u30E2\u30FC\u30C9", "\u30A8\u30F3\u30CF\u30F3\u30B9\u30C9\u30C6\u30AD\u30B9\u30C8\u30E2\u30FC\u30C9", "\u30CD\u30C3\u30C8\u30EF\u30FC\u30AF\u30A4\u30B7\u30E5\u30FC", "\u63A5\u7D9A\u5931\u6557", "\u30BB\u30C3\u30B7\u30E7\u30F3\u60C5\u5831", "\u30BB\u30C3\u30B7\u30E7\u30F3\u6570\u306E\u4E0A\u9650\u306B\u9054\u3057\u307E\u3057\u305F", "EOF(End Of File)", "\u30B9\u30AF\u30EA\u30FC\u30F3\u306E\u30EA\u30D5\u30EC\u30C3\u30B7\u30E5", 
            "\u8A18\u9332\u958B\u59CB", "\u8A18\u9332\u505C\u6B62", "\u82F1\u8A9E\u30AD\u30FC\u30DC\u30FC\u30C9", "\u4E2D\u56FD\u8A9E\uFF08\u7E41\u4F53\u5B57\uFF09\u30AD\u30FC\u30DC\u30FC\u30C9", "\u65E5\u672C\u8A9E\u30AD\u30FC\u30DC\u30FC\u30C9", "\u30C9\u30A4\u30C4\u8A9E\u30AD\u30FC\u30DC\u30FC\u30C9", "\u30D5\u30E9\u30F3\u30B9\u8A9E\u30AD\u30FC\u30DC\u30FC\u30C9", "\u30B9\u30DA\u30A4\u30F3\u8A9E\u30AD\u30FC\u30DC\u30FC\u30C9", "\u97D3\u56FD\u8A9E\u30AD\u30FC\u30DC\u30FC\u30C9", "\u30A4\u30BF\u30EA\u30A2\u8A9E\u30AD\u30FC\u30DC\u30FC\u30C9", 
            "\u30AD\u30E3\u30D7\u30C1\u30E3\u30FC", "\u30D5\u30EB\u30B9\u30AF\u30EA\u30FC\u30F3\u30D3\u30E5\u30FC", "\u30A6\u30A3\u30F3\u30C9\u30A6\u306E\u81EA\u52D5\u30EA\u30B5\u30A4\u30BA", "\u30C7\u30D0\u30A4\u30B9\u8A2D\u5B9A3", "\u30C7\u30D0\u30A4\u30B91", "\u30C7\u30D0\u30A4\u30B92", "\u30C7\u30D0\u30A4\u30B93", "United Kingdom\u8A9E\u30AD\u30FC\u30DC\u30FC\u30C9"
        };
        public static String GERUIString[] = {
            "Beenden", "Ja", "Nein", "Sind Sie sicher?", "Virtuelles Ger\344t", "Aufnahme", "Wiedergabe", "Makro", "Optionen", "Skalieren", 
            "Benutzerliste", "Hotkey-Einstellungen", "Aktion", "Hotkeys", "Tastatur\374berwachung", "Start", "Stopp", "Zuordnen", "Schlie\337en", "Eingabe", 
            "Bildschirm", "Ereignis wechseln", "Mausmodus", "Tastaturmodus", "Wartezeit f\374r Doppeltastendruck", "Vergr\366\337erungsfaktor", "Grafikqualit\344t", "Niedrig", "Hoch", "Einpassen", 
            "OSD-Men\374 verschieben", "Einstellungen f\374r Ger\344t 1", "Einstellungen f\374r Ger\344t 2", "Art des logischen Laufwerks", "Verbinden", "Anschlie\337en", "Trennen", "Verbindungsverlauf", "Bild \366ffnen", "Dateiname und Pfad des Bildes", 
            "Spracheinstellung", "Aktualisieren", "OK", "\326ffnen", "Wiedergabe/Pause", "Voreinstellung", "Tastatur-/Maus-Hotplug", "Benutzerliste anzeigen", "Minute(n)", "Abbrechen", 
            "Sitzungs-ID", "Benutzername", "Gedr\374ckt halten", "Dr\374cken und los lassen", "Verbindung aufgrund Web-Abmeldung getrennt", "Mauseinstellungen", "Absolute Maus (Windows)", "Relative Maus (Linux)", "Mauseingabe aktivieren", "Tastatureinstellungen", 
            "Tastatureingabe aktivieren", "Anzeige", "Leistung", "FPS-Steuerung aktivieren", "Frames pro Sekunde (fps):", "Aufnahmedauer", "Automatisch stoppen nach ", "Anzeigeskalierung", "Virtuelles Speicherger\344t", "Virtueller Datentr\344ger", 
            "USB Floppy und Flash", "CDROM und ISO", "Trennen", "IP-Adresse", "Maus einstellen", "Steuerung der Gegenstelle beenden", "Vollbildanzeige", "Refresh Window", "Strg+Alt+Entf senden", "Mausanzeige umschalten", 
            "Benutzerschnittstellenanzeige umschalten", "Rechte Alt-Taste gedr\374ckt halten", "Linke Alt-Taste gedr\374ckt halten", "Rechte Windows-Taste", "Linke Windows-Taste", "Leer", "Enter", "Trennung", "OSD-Benutzerschnittstelle", "Men\374leiste", 
            "Vollbildanzeige beibehalten", "Virtuelle Tastatur", "Normaler Betrieb", "Erweiterter Textmodus", "Netzwerkproblem", "Verbindungsfehler", "Sitzungsproblem", "Die maximale Anzahl Sitzungen wurde erreicht", "Dateiende (EOF)", "Bild aktualisieren", 
            "Aufnahme starten", "Aufnahme beenden", "Englische Tastatur", "Chinesische Tastatur (traditionell)", "Japanische Tastatur", "Deutsche Tastatur", "Franz\366sische Tastatur", "Spanische Tastatur", "Koreanische Tastatur", "Italienische Tastatur", 
            "Aufnahme", "Vollbildansicht", "Fenstergr\366\337e automatisch", "Einstellungen f\374r Ger\344t 3", "Ger\344t 1", "Ger\344t 2", "Ger\344t 3", "United Kingdom Tastatur"
        };
        public static String FRUIString[] = {
            "quitter", "oui", "non", "\352tes-vous s\373r\240?", "p\351riph\351rique virtuel", "enregistrer", "ex\351cuter", "macro", "options", "redimensionner", 
            "Liste d\u2019utilisateurs", "param\350tres raccourcis", "Action", "raccourcis", "contr\364le clavier", "d\351marrer", "arr\352ter", "attribuer", "fermer", "entr\351e", 
            "graphique", "\351v\351nement de basculement", "mode souris", "mode clavier", "d\351lai de r\351p\351tition de touche", "\351chelle de l\u2019image", "qualit\351 de l\u2019image", "faible", "\351lev\351e", "ajuster \340 la fen\352tre", 
            "d\351placer OSD", "param\350tres pour le p\351riph\351rique 1", "param\350tres pour le p\351riph\351rique 2", "type du disque logique", "connecter", "brancher", "d\351brancher", "historique de l\u2019\351tat de connexion", "ouvrir image", "nom de fichier et chemin d\u2019acc\350s complet de l\u2019image", 
            "Langue", "Actualiser", "OK", "Ouvrir", "Lire/Pause", "Pr\351f\351rences", "Connexion \340 chaud clavier/souris", "Afficher la liste des utilisateurs", "minute(s)", "Annuler", 
            "ID Session", "Nom d\u2019utilisateur", "Maintenir enfonc\351", "Appuyer et rel\342cher", "D\351connexion par terminaison de session web", "Param\350tres de souris", "Souris absolue (Windows)", "Souris relative (Linux)", "Activer l\u2019entr\351e souris", "Param\350tres de clavier", 
            "Activer l\u2019entr\351e clavier", "Affichage", "Performance", "Activer le contr\364le FPS", "cadre par seconde (FPS)", "Temps d\u2019enregistrement", "Activer l\u2019arr\352t automatique apr\350s ", "\311chelle d\u2019affichage", "Stockage virtuel", "Support virtuel", 
            "Disquette et Flash USB", "CD-ROM & ISO", "D\351brancher", "Adresse IP", "R\351gler la souris", "Quitter l\u2019emplacement distant", "Mode plein \351cran", "Refresh Window", "Envoyer Ctrl+Alt+Suppr", "Basculer l\u2019affichage de la souris", 
            "Basculer l\u2019affichage de l\u2019interface", "Tenez enfonc\351e la touche Alt de droite", "Tenez enfonc\351e la touche Alt de gauche", "Touche Windows de droite", "Touche Windows de gauche", "Espace", "Entr\351e", "Tiret", "Style d\u2019interface OSD", "Style d\u2019interface barre de menu", 
            "Quitter le mode plein \351cran", "Clavier virtuel", "Mode normal", "Mode de texte am\351lior\351", "Probl\350me r\351seau", "Echec de connexion", "Probl\350me de session", "Le nombre maximum de sessions est atteint", "EOF (Fin du fichier)", "Rafra\356chir l\u2019\351cran", 
            "Commencer l\u2019enregistrement", "Arr\352ter l\u2019enregistrement", "Clavier anglais", "Clavier chinois (traditionnel)", "Clavier japonais", "Clavier allemand", "Clavier fran\347ais", "Clavier espagnol", "Clavier cor\351en", "Clavier italien", 
            "Capturer", "Mode plein \351cran", "Redimensionnement automatique de la fen\352tre", "param\350tres pour le p\351riph\351rique 3", "p\351riph\351rique 1", "p\351riph\351rique 2", "p\351riph\351rique 3", "Clavier United Kingdom"
        };
        public static String SPUIString[] = {
            "Salir", "S\355", "No", "\277Est\341 seguro?", "Dispositivo virtual", "Grabar", "Reproducir", "Macro", "Opciones", "Escalar", 
            "Lista de usuarios", "Ajustes de teclas de acceso directo", "Acci\363n", "Teclas de acceso directo", "Monitorizaci\363n del teclado", "Iniciar", "Detener", "Asignar", "Cerrar", "Entrada", 
            "Imagen", "Cambiar evento", "Modo de rat\363n", "Modo de teclado", "Intervalo de repetici\363n de tecla", "Escala de imagen", "Calidad de imagen", "Baja", "Alta", "Ajustar a ventana", 
            "Mover OSD", "Ajustes para dispositivo 1", "Ajustes para dispositivo 2", "Tipo de unidad l\363gica", "Conectar", "Enchufar", "Desenchufar", "Historial del estado de conexi\363n", "Abrir imagen", "Nombre y ruta del archivo de imagen", 
            "Ajustar idioma", "Actualizar", "Aceptar", "Abrir", "Reproducir/Pausa", "Preferencias", "Conexi\363n en caliente de teclado/rat\363n", "Mostrar lista de usuarios", "minuto(s)", "Cancelar", 
            "ID de sesi\363n", "Nombre de usuario", "Mantener pulsada", "Pulsar y soltar", "Desconexi\363n por fin de sesi\363n Web", "Configuraci\363n de rat\363n", "Rat\363n absoluto (Windows)", "Rat\363n relativo (Linux)", "Activar introducci\363n por rat\363n", "Ajustes de teclado", 
            "Activar introducci\363n por teclado", "Dispositivo de visualizaci\363n", "Rendimiento", "Activar control FPS", "fotogramas por segundo (fps)", "Tiempo de grabaci\363n", "Detenci\363n autom\341tica tras ", "Escala de visualizaci\363n", "Almacenamiento virtual", "Soportes virtuales", 
            "Disquetera y flash USB", "CDROM e ISO", "Desconectar", "Direcci\363n IP", "Ajustar rat\363n", "Salir del sitio remoto", "Vista a pantalla completa", "Refresh Window", "Enviar Ctrl+Alt+Supr", "Cambiar aspecto de rat\363n", 
            "Cambiar aspecto de interfaz del usuario", "Mantener tecla Alt derecha pulsada", "Mantener tecla Alt izquierda pulsada", "Tecla Windows derecha", "Tecla Windows izquierda", "Espacio", "Intro", "Gui\363n", "Interfaz del usuario de tipo OSD", "Interfaz del usuario de tipo barra de men\372s", 
            "Mantener vista a pantalla completa", "Teclado virtual", "Modo normal", "Modo extendido de texto", "Problema de red", "Error de conexi\363n", "Problema de sesi\363n", "Se ha alcanzado el n\372mero m\341ximo de sesiones", "Fin de archivo (EOF)", "Actualizar pantalla", 
            "Iniciar grabaci\363n", "Detener grabaci\363n", "Teclado ingl\351s", "Teclado chino (tradicional)", "Teclado japon\351s", "Teclado alem\341n", "Teclado franc\351s", "Teclado espa\361ol", "Teclado coreano", "Teclado italiano", 
            "Capturar", "Vista a pantalla completa", "Redimensionar ventana autom\341ticamente", "Ajustes para dispositivo 3", "dispositivo 1", "dispositivo 2", "dispositivo 3", "Teclado United Kingdom"
        };
        public static String KORUIString[] = {
            "\uB05D\uB0B4\uAE30", "\uC608", "\uC544\uB2C8\uC624", "\uD655\uC2E4\uD569\uB2C8\uAE4C?", "\uAC00\uC0C1 \uC7A5\uCE58", "\uAE30\uB85D", "\uC7AC\uC0DD", "\uB9E4\uD06C\uB85C", "\uC635\uC158", "\uD06C\uAE30", 
            "\uC0AC\uC6A9\uC790 \uBAA9\uB85D", "\uD56B\uD0A4 \uC124\uC815", "\uB3D9\uC791", "\uD56B\uD0A4 ", "\uD0A4\uBCF4\uB4DC \uBAA8\uB2C8\uD130 ", "\uC2DC\uC791", "\uC815\uC9C0", "\uD560\uB2F9", "\uB2EB\uAE30", "\uC785\uB825", 
            "\uBE44\uB514\uC624", "\uD1A0\uAE00 \uC774\uBCA4\uD2B8", "\uB9C8\uC6B0\uC2A4 \uBAA8\uB4DC", "\uD0A4\uBCF4\uB4DC \uBAA8\uB4DC", "\uD0A4 \uD0C0\uC784\uC544\uC6C3 \uBC18\uBCF5 \uC2E4\uD589", "\uC774\uBBF8\uC9C0 \uD06C\uAE30", "\uC774\uBBF8\uC9C0 \uD488\uC9C8", "\uB0AE\uC74C", "\uB192\uC74C", "\uC708\uB3C4\uC6B0\uC5D0 \uB9DE\uCD94\uB2E4", 
            "OSD  \uC774\uB3D9", "\uC7A5\uCE58 1 \uC124\uC815", "\uC7A5\uCE58 2 \uC124\uC815", "\uB17C\uB9AC \uB4DC\uB77C\uC774\uBE0C \uC720\uD615", "\uC811\uC18D", "\uC5F0\uACB0\uB428 ", "\uC5F0\uACB0 \uC548\uB428", "\uC811\uC18D \uC0C1\uD0DC \uD788\uC2A4\uD1A0\uB9AC", "\uC774\uBBF8\uC9C0 \uC5F4\uAE30", "\uC774\uBBF8\uC9C0 \uD30C\uC77C\uBA85 \uBC0F \uC804\uCCB4 \uACBD\uB85C", 
            "\uC5B8\uC5B4 \uC124\uC815", "\uC0C8\uB85C\uACE0\uCE68", "\uD655\uC778", "\uC5F4\uAE30", "\uC7AC\uC0DD/\uC77C\uC2DC\uC815\uC9C0", "\uC120\uD638", "\uD0A4\uBCF4\uB4DC \uB9C8\uC6B0\uC2A4 \uD56B\uD50C\uB7EC\uADF8", "\uC0AC\uC6A9\uC790 \uBAA9\uB85D \uBCF4\uC5EC\uC8FC\uAE30", "\uBD84", "\uCDE8\uC18C", 
            "\uC138\uC158 ID", "\uC0AC\uC6A9\uC790 \uC774\uB984", "\uB204\uB978 \uC0C1\uD0DC", "\uB20C\uB800\uB2E4 \uB5BC\uC138\uC694. ", "\uC6F9 \uB85C\uADF8\uC544\uC6C3\uC73C\uB85C \uC778\uD574 \uC5F0\uACB0 \uB04A\uAE40", "\uB9C8\uC6B0\uC2A4 \uC124\uC815", "Absolute Mouse (Windows)", "Relative Mouse (Linux)", "\uB9C8\uC6B0\uC2A4 \uC785\uB825 \uC0AC\uC6A9", "\uD0A4\uBCF4\uB4DC \uC124\uC815", 
            "\uD0A4\uBCF4\uB4DC \uC785\uB825 \uC0AC\uC6A9", "\uB514\uC2A4\uD50C\uB808\uC774", "\uC2E4\uD589", "FPS \uC81C\uC5B4 \uC0AC\uC6A9", "\uCD08\uB2F9 \uD504\uB808\uC784 \uC218 (FPS)", "\uC2DC\uAC04 \uAE30\uB85D", "\uC790\uB3D9 \uC815\uC9C0 \uC0AC\uC6A9 ", "\uB514\uC2A4\uD50C\uB808\uC774 \uBE44\uC728", "\uAC00\uC0C1 \uC2A4\uD1A0\uB9AC\uC9C0", "\uAC00\uC0C1 \uBBF8\uB514\uC5B4", 
            "USB \uD50C\uB85C\uD53C & \uD50C\uB798\uC26C", "CDROM&ISO", "\uC804\uC6D0 \uB044\uAE30", "IP \uC8FC\uC18C", "\uB9C8\uC6B0\uC2A4 \uC870\uC815", "\uC6D0\uACA9 \uC704\uCE58 \uB098\uAC00\uAE30", "\uD480\uC2A4\uD06C\uB9B0 \uBAA8\uB4DC", "Refresh Window", "Ctrl+Alt+Del \uC785\uB825", "\uB9C8\uC6B0\uC2A4 \uB514\uC2A4\uD50C\uB808\uC774 \uD1A0\uAE00", 
            "UI \uB514\uC2A4\uD50C\uB808\uC774 \uD1A0\uAE00", "\uC624\uB978\uCABD Alt \uD0A4 \uB204\uB984", "\uC67C\uCABD Alt \uD0A4 \uB204\uB984", "\uC624\uB978\uCABD \uC708\uB3C4\uC6B0 \uD0A4", "\uC67C\uCABD \uC708\uB3C4\uC6B0 \uD0A4", "\uC2A4\uD398\uC774\uC2A4", "\uC5D4\uD130", "\uD558\uC774\uD508", "OSD UI \uC2A4\uD0C0\uC77C", "\uBA54\uB274\uBC14 UI \uC2A4\uD0C0\uC77C", 
            "\uD480\uC2A4\uD06C\uB9B0 \uBAA8\uB4DC \uD574\uC81C", "\uAC00\uC0C1 \uD0A4\uBCF4\uB4DC", "\uC77C\uBC18 \uBAA8\uB4DC", "\uAC15\uD654\uB41C \uD14D\uC2A4\uD2B8 \uBAA8\uB4DC", "\uB124\uD2B8\uC6CC\uD06C \uC774\uC288", "\uC811\uC18D \uC2E4\uD328", "\uC138\uC158 \uC774\uC288", "\uC138\uC158 \uC218\uAC00 \uC774\uBBF8 \uCD5C\uB300\uC785\uB2C8\uB2E4.", "EOF (End of File)", "\uD654\uBA74 \uC0C8\uB85C\uACE0\uCE68", 
            "\uB808\uCF54\uB529 \uC2DC\uC791", "\uB808\uCF54\uB529 \uC911\uC9C0", "\uC601\uC5B4 \uD0A4\uBCF4\uB4DC", "\uC911\uAD6D\uC5B4 (\uBC88\uCCB4) \uD0A4\uBCF4\uB4DC", "\uC77C\uC5B4 \uD0A4\uBCF4\uB4DC", "\uB3C5\uC77C\uC5B4 \uD0A4\uBCF4\uB4DC", "\uD504\uB791\uC2A4\uC5B4 \uD0A4\uBCF4\uB4DC", "\uC2A4\uD398\uC778\uC5B4 \uD0A4\uBCF4\uB4DC", "\uD55C\uAD6D\uC5B4 \uD0A4\uBCF4\uB4DC", "\uC774\uD0C8\uC774\uC544\uC5B4 \uD0A4\uBCF4\uB4DC", 
            "\uCEA1\uCCD0", "\uD480 \uC2A4\uD06C\uB9B0 \uBDF0", "\uD654\uBA74\uCC3D \uC790\uB3D9 \uD06C\uAE30 \uC870\uC808", "\uC7A5\uCE58 3 \uC124\uC815", "\uC7A5\uCE58 1 ", "\uC7A5\uCE58 2 ", "\uC7A5\uCE58 3 ", "United Kingdom keyboard"
        };
        public static String ITAUIString[] = {
            "Esci", "S\354", "No", "Sei sicuro?", "Dispositivo virtuale", "Registrazione", "Riproduzione", "Macro", "Opzioni", "Rappresenta in scala", 
            "Elenco dell\u2019utente", "Impostazioni del tasto di scelta rapida", "Azione", "Tasti di scelta rapida", "Controllo tastiera", "Avvio", "Stop", "Assegna", "Chiudi", "Ingresso", 
            "Video", "Attiva evento", "Modalit\340 mouse", "Modalit\340 tastiera", "Ripeti timeout del tasto", "Rappresenta in scala l\u2019immagine", "Qualit\340 dell\u2019immagine", "Bassa", "Alta", "Adatta alla finestra", 
            "Sposta osd", "Impostazioni del dispositivo 1", "Impostazioni del dispositivo 2", "Tipo di unit\340 logica", "Connetti", "Connetti", "Disconnetti", "Storico del collegamento", "Apri immagine", "Nome del file d\u2019immagine e percorso completo", 
            "Impostazioni lingua", "Aggiorna", "OK", "Apri", "Riproduci/Pausa", "Preferenze", "Collegamento tastiera/mouse a caldo", "Mostra elenco utenti", "minuto/i", "Annulla", 
            "ID sessione", "Nome utente", "Tieni premuto", "Premi e rilascia", "disconnesso per logout web", "Impostazioni mouse", "Mouse assoluto (Windows)", "Mouse relativo (Linux)", "Abilita input mouse", "Impostazioni tastiera", 
            "Abilita input tastiera", "Schermo", "Prestazioni", "Abilita controllo FPS", "fotogrammi al secondo (FPS)", "Tempo di registrazione", "Abilita stop automatico dopo ", "Scala schermo", "Memoria di massa virtuale", "Media virtuali", 
            "Floppy & flash USB", "CDROM&ISO", "Scollega", "Indirizzo IP", "Regola mouse", "Esci da sito remoto", "Modalit\340 schermo intero", "Refresh Window", "Invia Ctrl+Alt+Canc", "(Dis)attiva visualizzazione mouse", 
            "(Dis)attiva visualizzazione interfaccia", "Tieni premuto Alt destro", "Tieni premuto Alt sinistro", "Tasto Windows destro", "Tasto Windows sinistro", "Spazio", "Invio", "Trattino", "Vecchio stile interfaccia OSD", "Stile interfaccia barra dei menu", 
            "Esci dalla modalit\340 schermo intero", "Tastiera virtuale", "Modalit\340 normale", "Modalit\340 testo speciale", "Problema di rete", "Connessione fallita", "Problema sessione", "Raggiunto limite massimo sessioni", "EOF(End Of File)", "Aggiorna schermo", 
            "Avvia registrazione", "Interrompi registrazione", "Tastiera inglese", "Tastiera cinese (tradizionale)", "Tastiera giapponese", "Tastiera tedesca", "Tastiera francese", "Tastiera spagnola", "Tastiera coreana", "Tastiera italiana", 
            "Cattura", "Vista a pieno schermo", "Autoridimensiona finestra", "Impostazioni del dispositivo 3", "dispositivo 1", "dispositivo 2", "dispositivo 3", "Tastiera United Kingdom"
        };
        public static String UIStringUnion[][];

        static 
        {
            UIStringUnion = (new String[][] {
                ENUIString, JPUIString, GERUIString, FRUIString, SPUIString, KORUIString, ITAUIString
            });
        }

        public UILanguage()
        {
        }
    }

    public static class VMStatusLanguage
    {

        public static String ENVMString[] = {
            "", "Authentication Fail", "System Busy", "Privilege Error", "VM Plug-Out OK!! Stop!!", "The Length of Name is not correct", "The Length of password is not correct", "VM Device is not valid, please click \"Refresh\" button", "The Filename Extension of ISO Image File is not correct", "The Filename Extension of Floppy Image File is not correct", 
            "Can not open ISO Image File", "Can not open Floppy Image File", "Can not connect to Server!!", "VM Plug-In OK!!", "Invalid Device Selection", "Don't access file on Local storage device", "Virtual Media Plug In fail!", "The size of the floppy image is too large", "No response, please try again!", "Response error, please try again!", 
            "Upload Success!", "Plug out Success!", "Exist IMA File on BMC", "Exist an effective Connect from others", "The Device doesn't Match in Client Computer", "The Device Operates Error !!", "Virtual Media is in Detach Mode!!", "Open Exist Device Error!!\nPlease Check if a valid data in the device", "Synchronize Storage Contents!!\nPlease Wait a moment!!", "Mount Web ISO Success!", 
            "UnMount Web ISO Success!", "Media has been unmounted from others", "Session ID is Expired", "BMC is in FW updating", "Session ID is Expired", "VM Process Busy!Please Try again!", "Plese Check if any Disc in the Device!"
        };
        public static String JPVMString[] = {
            "", "", "", "", "", "", "", "", "", "", 
            "", "", "", "", "", "", "", "", "", "", 
            "", "", "", "", "", "", "", "", "", "", 
            "", ""
        };
        public static String GERVMString[] = {
            "", "", "", "", "", "", "", "", "", "", 
            "", "", "", "", "", "", "", "", "", "", 
            "", "", "", "", "", "", "", "", "", "", 
            "", ""
        };
        public static String FRVMString[] = {
            "", "", "", "", "", "", "", "", "", "", 
            "", "", "", "", "", "", "", "", "", "", 
            "", "", "", "", "", "", "", "", "", "", 
            "", ""
        };
        public static String SPVMString[] = {
            "", "", "", "", "", "", "", "", "", "", 
            "", "", "", "", "", "", "", "", "", "", 
            "", "", "", "", "", "", "", "", "", "", 
            "", ""
        };
        public static String KORVMString[] = {
            "", "", "", "", "", "", "", "", "", "", 
            "", "", "", "", "", "", "", "", "", "", 
            "", "", "", "", "", "", "", "", "", "", 
            "", ""
        };
        public static String ITAVMString[] = {
            "", "", "", "", "", "", "", "", "", "", 
            "", "", "", "", "", "", "", "", "", "", 
            "", "", "", "", "", "", "", "", "", "", 
            "", ""
        };
        public static String SIMCNVMString[] = {
            "", "\u8BA4\u8BC1\u5931\u8D25", "\u7CFB\u7EDF\u5FD9", "\u6743\u9650\u9519\u8BEF", "\u865A\u62DF\u4ECB\u8D28\u5378\u8F7D\u5B8C\u6210", "\u8D26\u6237\u540D\u957F\u5EA6\u9519\u8BEF", "\u5BC6\u7801\u957F\u5EA6\u9519\u8BEF", "\u6CA1\u6709\u53D1\u73B0\u53EF\u7528\u7684\u865A\u62DF\u4ECB\u8D28\uFF0C\u8BF7\u6309\u5237\u65B0\u952E\u91CD\u8BD5", "ISO\u5F71\u50CF\u6587\u4EF6\u6269\u5C55\u540D\u9519\u8BEF", "\u8F6F\u76D8\u5F71\u50CF\u6587\u4EF6\u6269\u5C55\u540D\u79F0\u9519\u8BEF", 
            "\u65E0\u6CD5\u6253\u5F00ISO\u955C\u50CF", "\u65E0\u6CD5\u6253\u5F00\u8F6F\u76D8\u955C\u50CF", "\u65E0\u6CD5\u8FDE\u63A5\u670D\u52A1\u5668", "\u865A\u62DF\u4ECB\u8D28\u52A0\u8F7D\u6210\u529F", "\u88C5\u7F6E\u9009\u62E9\u9519\u8BEF", "\u8BF7\u4E0D\u8981\u8BBF\u95EE\u8BE5\u672C\u5730\u5B58\u50A8\u8BBE\u5907\u7684\u6587\u4EF6", "\u865A\u62DF\u4ECB\u8D28\u52A0\u8F7D\u5931\u8D25", "\u8BE5\u8F6F\u76D8\u955C\u50CF\u592A\u5927", "\u65E0\u54CD\u5E94\uFF0C\u8BF7\u518D\u8BD5\u4E00\u6B21", "\u54CD\u5E94\u9519\u8BEF\uFF0C\u8BF7\u518D\u8BD5\u4E00\u6B21", 
            "\u4E0A\u8F7D\u5B8C\u6210", "\u5378\u8F7D\u5B8C\u6210", "IMA\u6587\u4EF6\u5DF2\u7ECF\u52A0\u8F7D", "\u5DF2\u7ECF\u5B58\u5728\u4E00\u4E2A\u6709\u6548\u7684\u8FDE\u63A5", "\u8BE5\u8BBE\u5907\u4E0E\u5BA2\u6237\u7AEF\u7684\u8BA1\u7B97\u673A\u4E0D\u7B26\u5408", "\u8BBE\u5907\u64CD\u4F5C\u9519\u8BEF", "\u865A\u62DF\u4ECB\u8D28\u5728\u5206\u79BB\u6A21\u5F0F", "\u6253\u5F00\u8BBE\u5907\u9519\u8BEF\uFF0C\u8BF7\u786E\u8BA4\u662F\u8BBE\u5907\u4E2D\u662F\u5426\u6709\u6709\u6548\u6570\u636E", "\u6B63\u5728\u540C\u6B65\u5B58\u50A8\u5185\u5BB9\uFF0C\u8BF7\u7A0D\u5019", "\u52A0\u8F7D\u7F51\u7EDCISO\u955C\u50CF\u6210\u529F", 
            "\u5378\u8F7D\u7F51\u7EDCISO\u955C\u50CF\u6210\u529F!", "\u865A\u62DF\u4ECB\u8D28\u5DF2\u7ECF\u88AB\u5176\u5B83\u5BA2\u6237\u7AEF\u5378\u8F09", "\u4F1A\u8BDD\u8FDE\u63A5\u5DF2\u7ECF\u8FC7\u671F\u5931\u6548", "BMC\u56FA\u4EF6\u6B63\u5728\u66F4\u65B0", "\u4F1A\u8BDD\u8FDE\u63A5\u5DF2\u7ECF\u8FC7\u671F\u5931\u6548", "\u865A\u62DF\u4ECB\u8D28\u5904\u7406\u5FD9\uFF0C\u8BF7\u91CD\u8BD5", "\u8BF7\u786E\u8BA4\u5149\u9A71\u4E2D\u662F\u5426\u6709\u5149\u76D8"
        };
        public static String VMStringUnion[][];

        static 
        {
            VMStringUnion = (new String[][] {
                ENVMString, JPVMString, GERVMString, FRVMString, SPVMString, KORVMString, ITAVMString, SIMCNVMString
            });
        }

        public VMStatusLanguage()
        {
        }
    }


    public InfoRepository()
    {
        lang_idx = 0;
        connInfo = new ConnInfo();
        userInfo = new UserInfo();
        platformInfo = new PlatformInfo();
        vusbInfo = new VirtualUsbInfo(3);
        iconMap = new Hashtable();
        try
        {
            Class class1 = getClass();
        }
        catch(NullPointerException nullpointerexception)
        {
            nullpointerexception.printStackTrace();
        }
    }

    public ConnInfo getConnInfo()
    {
        return connInfo;
    }

    public UserInfo getUserInfo()
    {
        return userInfo;
    }

    public PlatformInfo getPlatformInfo()
    {
        return platformInfo;
    }

    public VirtualUsbInfo getVirtualUsbInfo()
    {
        return vusbInfo;
    }

    public String[] getLangType()
    {
        return langType;
    }

    public String[] getUIString()
    {
        return UILanguage.UIStringUnion[0];
    }

    public String[] getUIString(int i)
    {
        return UILanguage.UIStringUnion[i];
    }

    public String[] getVMStatusString()
    {
        return VMStatusLanguage.VMStringUnion[0];
    }

    public String[] getVMStatusString(int i)
    {
        return VMStatusLanguage.VMStringUnion[i];
    }

    public int getLangIdx()
    {
        return lang_idx;
    }

    public void setLangIdx(int i)
    {
        lang_idx = i;
    }

    private static String icons[] = {
        "/res/alluser.png", "/res/caps1.png", "/res/caps3.png", "/res/end.png", "/res/Exit.jpg", "/res/fullsize.png", "/res/hotPlug.jpg", "/res/home.png", "/res/katakana.png", "/res/macro.jpg", 
        "/res/menu.png", "/res/MoveOSD.jpg", "/res/nocolor.jpg", "/res/Option.jpg", "/res/pgdn.png", "/res/pgup.png", "/res/ret.png", "/res/ret2.png", "/res/scale.png", "/res/SetHotkey.jpg", 
        "/res/shift.png", "/res/shift3.png", "/res/tab.png", "/res/ToggleUI.jpg", "/res/VirtualKeyboard.jpg", "/res/VirtualStorage.jpg", "/res/wnd.png", "/res/record.png", "/res/play.png"
    };
    public static final int numUIString = 39;
    private ConnInfo connInfo;
    private UserInfo userInfo;
    private PlatformInfo platformInfo;
    private VirtualUsbInfo vusbInfo;
    private Hashtable iconMap;
    private int lang_idx;
    private String langType[] = {
        "English", "Japanese", "Deutsch", "Fran\347ais", "Espa\361ol", "\uD55C\uAD6D\uC5B4", "Italiano"
    };

}
