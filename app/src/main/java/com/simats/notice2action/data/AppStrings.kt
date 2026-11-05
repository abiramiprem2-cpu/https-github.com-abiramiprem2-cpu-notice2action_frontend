package com.simats.notice2action.data

object AppStrings {
    // English (Default)
    private val en = mapOf(
        "nav_home" to "Home",
        "nav_tasks" to "Tasks",
        "nav_reminders" to "Reminders",
        "nav_profile" to "Profile",

        // Dashboard
        "dash_category" to "B • MAIN DASHBOARD & NAVIGATION",
        "dash_good_morning" to "Good Morning",
        "dash_banner_title" to "TURN DOCUMENTS\nINTO ACTIONS.",
        "dash_banner_sub" to "Your next step, without the guesswork.",
        "dash_scan_document" to "Scan Document",
        "dash_upload_document" to "Upload Document",
        "dash_docs_processed" to "Documents Processed",
        "dash_upcoming_actions" to "Upcoming Actions",
        "dash_recent_docs" to "Recent Documents",
        "dash_view_all" to "View all",
        "dash_upcoming_deadlines" to "Upcoming Deadlines",
        "dash_calendar" to "Calendar",
        "dash_verify_dates" to "Verify dates",
        "dash_actions_identified" to "actions identified",

        // Navigation Drawer
        "menu_home" to "Home",
        "menu_documents" to "My Documents",
        "menu_tasks" to "My Tasks",
        "menu_reminders" to "Reminders",
        "menu_calendar" to "Calendar",
        "menu_groups" to "Groups",
        "menu_settings" to "Settings",
        "menu_help" to "Help and Support",
        "menu_logout" to "Logout",

        // Profile Screen
        "prof_category" to "H • PROFILE, SETTINGS & SUPPORT",
        "prof_title" to "My Profile",
        "prof_edit_profile" to "Edit Profile",
        "prof_notifications" to "Notifications",
        "prof_reminders_enabled" to "2 reminders enabled",
        "prof_language" to "Language",
        "prof_account_settings" to "Account settings",
        "prof_account_settings_sub" to "Personal details & security",
        "prof_privacy_data" to "Privacy & data",
        "prof_privacy_data_sub" to "Your documents, your control",
        "prof_help_support" to "Help and Support",
        "prof_help_support_sub" to "Guides and contact options",
        "prof_logout" to "Logout",

        // Edit Profile Dialog
        "edit_prof_title" to "Edit Profile",
        "edit_prof_choose_photo" to "Choose Photo from Gallery",
        "edit_prof_full_name" to "Full Name",
        "edit_prof_enter_name" to "Enter your name",
        "edit_prof_email" to "Email ID",
        "edit_prof_enter_email" to "Enter your email",
        "edit_prof_save" to "Save Changes",
        "edit_prof_saving" to "Saving...",
        "edit_prof_cancel" to "Cancel",
        "edit_prof_success" to "Profile updated successfully!",

        // Settings & Privacy Screen
        "settings_title" to "Settings & privacy",
        "settings_appearance" to "Appearance",
        "settings_light" to "Light",
        "settings_dark" to "Dark",
        "settings_system" to "System",
        "settings_default_reminders" to "Default reminders",
        "settings_default_reminders_sub" to "1 day before - 9:00 AM",
        "settings_doc_privacy" to "Document privacy",
        "settings_doc_privacy_sub" to "Private unless you explicitly share",
        "settings_data_export" to "Data export",
        "settings_data_export_sub" to "Documents and tasks - Export ZIP",
        "settings_info_banner" to "Sharing requires recipient authorization. No data is stored or exported without your permission.",
        "settings_account_control" to "Account control",
        "settings_delete_account" to "Delete Account",
        "settings_delete_account_desc" to "Permanently remove your profile, private documents, tasks and reminders.",

        // Language Screen
        "lang_title" to "Language",
        "lang_subtitle" to "Choose your preferred language",

        // Documents & Actions
        "upload_doc_title" to "Upload Document",
        "scan_doc_title" to "Scan Document",
        "doc_preview_title" to "Document Preview",
        "notice_details_title" to "Notice Details",
        "review_actions_title" to "Review Suggested Actions",
        "upcoming_actions_screen_title" to "Upcoming Actions",
        "action_continue" to "Continue",
        "action_back" to "Back",

        // Notice Analysis Q&A
        "qa_what_about" to "What is this document about?",
        "qa_what_about_sub" to "AI Analysis in Simple Words",
        "qa_what_to_do" to "What do I need to do?",
        "qa_what_to_do_sub" to "Required Tasks & Action Items",
        "qa_by_when" to "By when should I do it?",
        "qa_by_when_sub" to "Important Deadlines & Timeline",
        "qa_extracted_ocr" to "Extracted Document Text (OCR)",
        "qa_copy_text" to "Copy Text",
        "qa_review_actions" to "Review & Confirm Actions",
        "qa_tasks_identified" to "Tasks Identified",
        "qa_primary_deadline" to "Primary Due Date"
    )

    // Tamil (தமிழ்)
    private val ta = mapOf(
        "nav_home" to "முகப்பு",
        "nav_tasks" to "பணிகள்",
        "nav_reminders" to "நினைவூட்டல்கள்",
        "nav_profile" to "சுயவிவரம்",

        // Dashboard
        "dash_category" to "B • முதன்மை முகப்பு & வழிசெலுத்தல்",
        "dash_good_morning" to "காலை வணக்கம்",
        "dash_banner_title" to "ஆவணங்களை\nசெயல்களாக மாற்றுங்கள்.",
        "dash_banner_sub" to "யூகங்கள் இல்லாமல் உங்கள் அடுத்த படி.",
        "dash_scan_document" to "ஆவணத்தை ஸ்கேன் செய்",
        "dash_upload_document" to "ஆவணத்தைப் பதிவேற்று",
        "dash_docs_processed" to "செயலாக்கப்பட்ட ஆவணங்கள்",
        "dash_upcoming_actions" to "வரவிருக்கும் செயல்கள்",
        "dash_recent_docs" to "சமீபத்திய ஆவணங்கள்",
        "dash_view_all" to "அனைத்தையும் பார்",
        "dash_upcoming_deadlines" to "வரவிருக்கும் காலக்கெடு",
        "dash_calendar" to "நாட்காட்டி",
        "dash_verify_dates" to "தேதிகளைச் சரிபார்",
        "dash_actions_identified" to "செயல்கள் கண்டறியப்பட்டன",

        // Navigation Drawer
        "menu_home" to "முகப்பு",
        "menu_documents" to "எனது ஆவணங்கள்",
        "menu_tasks" to "எனது பணிகள்",
        "menu_reminders" to "நினைவூட்டல்கள்",
        "menu_calendar" to "நாட்காட்டி",
        "menu_groups" to "குழுக்கள்",
        "menu_settings" to "அமைப்புகள்",
        "menu_help" to "உதவி & ஆதரவு",
        "menu_logout" to "வெளியேறு",

        // Profile Screen
        "prof_category" to "H • சுயவிவரம், அமைப்புகள் & ஆதரவு",
        "prof_title" to "எனது சுயவிவரம்",
        "prof_edit_profile" to "சுயவிவரத்தைத் திருத்து",
        "prof_notifications" to "அறிவிப்புகள்",
        "prof_reminders_enabled" to "2 நினைவூட்டல்கள் இயக்கப்பட்டுள்ளன",
        "prof_language" to "மொழி",
        "prof_account_settings" to "கணக்கு அமைப்புகள்",
        "prof_account_settings_sub" to "தனிப்பட்ட விவரங்கள் & பாதுகாப்பு",
        "prof_privacy_data" to "தனியுரிமை & தரவு",
        "prof_privacy_data_sub" to "உங்கள் ஆவணங்கள், உங்கள் கட்டுப்பாடு",
        "prof_help_support" to "உதவி & ஆதரவு",
        "prof_help_support_sub" to "வழிகாட்டிகள் & தொடர்பு விருப்பங்கள்",
        "prof_logout" to "வெளியேறு",

        // Edit Profile Dialog
        "edit_prof_title" to "சுயவிவரத்தைத் திருத்து",
        "edit_prof_choose_photo" to "கேலரியிலிருந்து புகைப்படத்தைத் தேர்ந்தெடு",
        "edit_prof_full_name" to "முழு பெயர்",
        "edit_prof_enter_name" to "உங்கள் பெயரை உள்ளிடவும்",
        "edit_prof_email" to "மின்னஞ்சல் முகவரி",
        "edit_prof_enter_email" to "உங்கள் மின்னஞ்சலை உள்ளிடவும்",
        "edit_prof_save" to "மாற்றங்களைச் சேமி",
        "edit_prof_saving" to "சேமிக்கப்படுகிறது...",
        "edit_prof_cancel" to "ரத்து செய்",
        "edit_prof_success" to "சுயவிவரம் வெற்றிகரமாக புதுப்பிக்கப்பட்டது!",

        // Settings & Privacy Screen
        "settings_title" to "அமைப்புகள் & தனியுரிமை",
        "settings_appearance" to "தோற்றம்",
        "settings_light" to "வெளிச்சம்",
        "settings_dark" to "இருள்",
        "settings_system" to "கணினி",
        "settings_default_reminders" to "இயல்புநிலை நினைவூட்டல்கள்",
        "settings_default_reminders_sub" to "1 நாளுக்கு முன் - 9:00 மு.ப",
        "settings_doc_privacy" to "ஆவணத் தனியுரிமை",
        "settings_doc_privacy_sub" to "நீங்கள் பகிரும் வரை தனிப்பட்டது",
        "settings_data_export" to "தரவு ஏற்றுமதி",
        "settings_data_export_sub" to "ஆவணங்கள் மற்றும் பணிகள் - ZIP ஏற்றுமதி",
        "settings_info_banner" to "பகிர்வதற்கு பெறுநரின் அங்கீகாரம் தேவை. உங்கள் அனுமதியின்றி எந்த தரவும் சேமிக்கப்படாது.",
        "settings_account_control" to "கணக்குக் கட்டுப்பாடு",
        "settings_delete_account" to "கணக்கை நீக்கு",
        "settings_delete_account_desc" to "உங்கள் சுயவிவரம், ஆவணங்கள், பணிகள் மற்றும் நினைவூட்டல்களை நிரந்தரமாக அகற்றவும்.",

        // Language Screen
        "lang_title" to "மொழி",
        "lang_subtitle" to "விருப்பமான மொழியைத் தேர்ந்தெடுக்கவும்",

        // Documents & Actions
        "upload_doc_title" to "ஆவணத்தைப் பதிவேற்று",
        "scan_doc_title" to "ஆவணத்தை ஸ்கேன் செய்",
        "doc_preview_title" to "ஆவண முன்னோட்டம்",
        "notice_details_title" to "அறிவிப்பு விவரங்கள்",
        "review_actions_title" to "பரிந்துரைக்கப்பட்ட செயல்கள்",
        "upcoming_actions_screen_title" to "வரவிருக்கும் செயல்கள்",
        "action_continue" to "தொடரவும்",
        "action_back" to "பின்செல்",

        // Notice Analysis Q&A
        "qa_what_about" to "இந்த ஆவணம் எதைப் பற்றியது?",
        "qa_what_about_sub" to "எளிய சொற்களில் AI விளக்கம்",
        "qa_what_to_do" to "நான் என்ன செய்ய வேண்டும்?",
        "qa_what_to_do_sub" to "தேவையான பணிகள் மற்றும் நடவடிக்கைகள்",
        "qa_by_when" to "நான் எப்போது வரை செய்ய வேண்டும்?",
        "qa_by_when_sub" to "முக்கிய காலக்கெடு மற்றும் தேதிகள்",
        "qa_extracted_ocr" to "ஆவண உரை (OCR)",
        "qa_copy_text" to "உரையை நகலெடு",
        "qa_review_actions" to "செயல்களை மதிப்பாய்வு செய்க",
        "qa_tasks_identified" to "பணிகள் கண்டறியப்பட்டன",
        "qa_primary_deadline" to "முக்கிய கடைசி தேதி"
    )

    // Malayalam (മലയാളം)
    private val ml = mapOf(
        "nav_home" to "ഹോം",
        "nav_tasks" to "ജോലികൾ",
        "nav_reminders" to "ഓർമ്മപ്പെടുത്തലുകൾ",
        "nav_profile" to "പ്രൊഫൈൽ",

        // Dashboard
        "dash_category" to "B • പ്രധാന ഡാഷ്‌ബോർഡ് & നാവിഗേഷൻ",
        "dash_good_morning" to "സുപ്രഭാതം",
        "dash_banner_title" to "രേഖകളെ\nപ്രവർത്തനങ്ങളാക്കി മാറ്റുക.",
        "dash_banner_sub" to "ഊഹങ്ങളില്ലാതെ നിങ്ങളുടെ അടുത്ത ഘട്ടം.",
        "dash_scan_document" to "രേഖ സ്കാൻ ചെയ്യുക",
        "dash_upload_document" to "രേഖ അപ്‌ലോഡ് ചെയ്യുക",
        "dash_docs_processed" to "പരിശോധിച്ച രേഖകൾ",
        "dash_upcoming_actions" to "വരാനിരിക്കുന്ന പ്രവർത്തനങ്ങൾ",
        "dash_recent_docs" to "സമീപകാല രേഖകൾ",
        "dash_view_all" to "എല്ലാം കാണുക",
        "dash_upcoming_deadlines" to "വരാനിരിക്കുന്ന സമയപരിധികൾ",
        "dash_calendar" to "കലണ്ടർ",
        "dash_verify_dates" to "തീയതികൾ സ്ഥിരീകരിക്കുക",
        "dash_actions_identified" to "പ്രവർത്തനങ്ങൾ കണ്ടെത്തി",

        // Navigation Drawer
        "menu_home" to "ഹോം",
        "menu_documents" to "എന്റെ രേഖകൾ",
        "menu_tasks" to "എന്റെ ജോലികൾ",
        "menu_reminders" to "ഓർമ്മപ്പെടുത്തലുകൾ",
        "menu_calendar" to "കലണ്ടർ",
        "menu_groups" to "ഗ്രൂപ്പുകൾ",
        "menu_settings" to "ക്രമീകരണങ്ങൾ",
        "menu_help" to "സഹായവും പിന്തുണയും",
        "menu_logout" to "ലോഗ് ഔട്ട്",

        // Profile Screen
        "prof_category" to "H • പ്രൊഫൈൽ, ക്രമീകരണങ്ങൾ & പിന്തുണ",
        "prof_title" to "എന്റെ പ്രൊഫൈൽ",
        "prof_edit_profile" to "പ്രൊഫൈൽ തിരുത്തുക",
        "prof_notifications" to "അറിയിപ്പുകൾ",
        "prof_reminders_enabled" to "2 ഓർമ്മപ്പെടുത്തലുകൾ സജീവം",
        "prof_language" to "ഭാഷ",
        "prof_account_settings" to "അക്കൗണ്ട് ക്രമീകരണങ്ങൾ",
        "prof_account_settings_sub" to "വ്യക്തിഗത വിവരങ്ങളും സുരക്ഷയും",
        "prof_privacy_data" to "സ്വകാര്യതയും വിവരങ്ങളും",
        "prof_privacy_data_sub" to "നിങ്ങളുടെ രേഖകൾ, നിങ്ങളുടെ നിയന്ത്രണം",
        "prof_help_support" to "സഹായവും പിന്തുണയും",
        "prof_help_support_sub" to "മാർഗ്ഗനിർദ്ദേശങ്ങളും ബന്ധപ്പെടാനുള്ള വഴികളും",
        "prof_logout" to "ലോഗ് ഔട്ട്",

        // Edit Profile Dialog
        "edit_prof_title" to "പ്രൊഫൈൽ തിരുത്തുക",
        "edit_prof_choose_photo" to "ഗാലറിയിൽ നിന്ന് ഫോട്ടോ തിരഞ്ഞെടുക്കുക",
        "edit_prof_full_name" to "പൂർണ്ണമായ പേര്",
        "edit_prof_enter_name" to "നിങ്ങളുടെ പേര് നൽകുക",
        "edit_prof_email" to "ഇമെയിൽ വിലാസം",
        "edit_prof_enter_email" to "നിങ്ങളുടെ ഇമെയിൽ നൽകുക",
        "edit_prof_save" to "മാറ്റങ്ങൾ സംരക്ഷിക്കുക",
        "edit_prof_saving" to "സംരക്ഷിക്കുന്നു...",
        "edit_prof_cancel" to "റദ്ദാക്കുക",
        "edit_prof_success" to "പ്രൊഫൈൽ വിജയകരമായി അപ്‌ഡേറ്റുചെയ്‌തു!",

        // Settings & Privacy Screen
        "settings_title" to "ക്രമീകരണങ്ങളും സ്വകാര്യതയും",
        "settings_appearance" to "രൂപഭാവം",
        "settings_light" to "ലൈറ്റ്",
        "settings_dark" to "ഡാർക്ക്",
        "settings_system" to "സിസ്റ്റം",
        "settings_default_reminders" to "ഡിഫോൾട്ട് ഓർമ്മപ്പെടുത്തലുകൾ",
        "settings_default_reminders_sub" to "1 ദിവസം മുമ്പ് - 9:00 AM",
        "settings_doc_privacy" to "രേഖകളുടെ സ്വകാര്യത",
        "settings_doc_privacy_sub" to "നിങ്ങൾ പങ്കിടുന്നത് വരെ സ്വകാര്യം",
        "settings_data_export" to "വിവരങ്ങൾ എക്‌സ്‌പോർട്ട് ചെയ്യുക",
        "settings_data_export_sub" to "രേഖകളും ജോലികളും - ZIP എക്‌സ്‌പോർട്ട്",
        "settings_info_banner" to "പങ്കിടുന്നതിന് അനുമതി ആവശ്യമാണ്. നിങ്ങളുടെ അനുമതിയില്ലാതെ വിവരങ്ങൾ സൂക്ഷിക്കില്ല.",
        "settings_account_control" to "അക്കൗണ്ട് നിയന്ത്രണം",
        "settings_delete_account" to "അക്കൗണ്ട് ഇല്ലാതാക്കുക",
        "settings_delete_account_desc" to "നിങ്ങളുടെ പ്രൊഫൈൽ, രേഖകൾ, ജോലികൾ എന്നിവ ശാശ്വതമായി നീക്കം ചെയ്യുക.",

        // Language Screen
        "lang_title" to "ഭാഷ",
        "lang_subtitle" to "നിങ്ങളുടെ ഭാഷ തിരഞ്ഞെടുക്കുക",

        // Documents & Actions
        "upload_doc_title" to "രേഖ അപ്‌ലോഡ് ചെയ്യുക",
        "scan_doc_title" to "രേഖ സ്കാൻ ചെയ്യുക",
        "doc_preview_title" to "രേഖ പ്രിവ്യൂ",
        "notice_details_title" to "അറിയിപ്പ് വിവരങ്ങൾ",
        "review_actions_title" to "നിർദ്ദേശിച്ച പ്രവർത്തനങ്ങൾ",
        "upcoming_actions_screen_title" to "വരാനിരിക്കുന്ന പ്രവർത്തനങ്ങൾ",
        "action_continue" to "തുടരുക",
        "action_back" to "തിരികെ",

        // Notice Analysis Q&A
        "qa_what_about" to "ഈ രേഖ എന്തിനെക്കുറിച്ചാണ്?",
        "qa_what_about_sub" to "ലളിതമായ വാക്കുകളിൽ AI വിശകലനം",
        "qa_what_to_do" to "ഞാൻ എന്താണ് ചെയ്യേണ്ടത്?",
        "qa_what_to_do_sub" to "ആവശ്യമായ ജോലികളും നടപടികളും",
        "qa_by_when" to "ഇത് എപ്പോഴേക്ക് ചെയ്യണം?",
        "qa_by_when_sub" to "പ്രധാന സമയപരിധികളും തീയതികളും",
        "qa_extracted_ocr" to "രേഖയിലെ വാചകം (OCR)",
        "qa_copy_text" to "വാചകം പകർത്തുക",
        "qa_review_actions" to "നടപടികൾ അവലോകനം ചെയ്യുക",
        "qa_tasks_identified" to "ജോലികൾ കണ്ടെത്തി",
        "qa_primary_deadline" to "പ്രധാന അവസാന തീയതി"
    )

    // Telugu (తెలుగు)
    private val te = mapOf(
        "nav_home" to "హోమ్",
        "nav_tasks" to "పనులు",
        "nav_reminders" to "రిమైండర్‌లు",
        "nav_profile" to "ప్రొఫైల్",

        // Dashboard
        "dash_category" to "B • ప్రధాన డ్యాష్‌బోర్డ్ & నావిగేషన్",
        "dash_good_morning" to "శుభోదయం",
        "dash_banner_title" to "పత్రాలను\nచర్యలుగా మార్చండి.",
        "dash_banner_sub" to "అంచనాలు లేకుండా మీ తదుపరి అడుగు.",
        "dash_scan_document" to "పత్రాన్ని స్కాన్ చేయండి",
        "dash_upload_document" to "పత్రాన్ని అప్‌లోడ్ చేయండి",
        "dash_docs_processed" to "ప్రాసెస్ చేసిన పత్రాలు",
        "dash_upcoming_actions" to "రాబోయే చర్యలు",
        "dash_recent_docs" to "ఇటీవలి పత్రాలు",
        "dash_view_all" to "అన్నీ చూడండి",
        "dash_upcoming_deadlines" to "రాబోయే గడువులు",
        "dash_calendar" to "క్యాలెండర్",
        "dash_verify_dates" to "తేదీలను ధృవీకరించండి",
        "dash_actions_identified" to "చర్యలు గుర్తించబడ్డాయి",

        // Navigation Drawer
        "menu_home" to "హోమ్",
        "menu_documents" to "నా పత్రాలు",
        "menu_tasks" to "నా పనులు",
        "menu_reminders" to "రిమైండర్‌లు",
        "menu_calendar" to "క్యాలెండర్",
        "menu_groups" to "సమూహాలు",
        "menu_settings" to "సెట్టింగ్‌లు",
        "menu_help" to "సహాయం & మద్దతు",
        "menu_logout" to "లాగౌట్",

        // Profile Screen
        "prof_category" to "H • ప్రొఫైల్, సెట్టింగ్‌లు & మద్దతు",
        "prof_title" to "నా ప్రొఫైల్",
        "prof_edit_profile" to "ప్రొఫైల్‌ను సవరించండి",
        "prof_notifications" to "నోటిఫికేషన్‌లు",
        "prof_reminders_enabled" to "2 రిమైండర్‌లు ప్రారంభించబడ్డాయి",
        "prof_language" to "భాష",
        "prof_account_settings" to "ఖాతా సెట్టింగ్‌లు",
        "prof_account_settings_sub" to "వ్యక్తిగత వివరాలు & భద్రత",
        "prof_privacy_data" to "గోప్యత & డేటా",
        "prof_privacy_data_sub" to "మీ పత్రాలు, మీ నియంత్రణ",
        "prof_help_support" to "సహాయం & మద్దతు",
        "prof_help_support_sub" to "గైడ్‌లు & సంప్రదింపు ఎంపికలు",
        "prof_logout" to "లాగౌట్",

        // Edit Profile Dialog
        "edit_prof_title" to "ప్రొఫైల్‌ను సవరించండి",
        "edit_prof_choose_photo" to "గ్యాలరీ నుండి ఫోటోను ఎంచుకోండి",
        "edit_prof_full_name" to "పూర్తి పేరు",
        "edit_prof_enter_name" to "మీ పేరు నమోదు చేయండి",
        "edit_prof_email" to "ఈమెయిల్ చిరునామా",
        "edit_prof_enter_email" to "మీ ఈమెయిల్ నమోదు చేయండి",
        "edit_prof_save" to "మార్పులను భద్రపరచండి",
        "edit_prof_saving" to "భద్రపరుస్తోంది...",
        "edit_prof_cancel" to "రద్దు చేయండి",
        "edit_prof_success" to "ప్రొఫైల్ విజయవంతంగా అప్‌డేట్ చేయబడింది!",

        // Settings & Privacy Screen
        "settings_title" to "సెట్టింగ్‌లు & గోప్యత",
        "settings_appearance" to "రూపురేఖలు",
        "settings_light" to "లైట్",
        "settings_dark" to "డార్క్",
        "settings_system" to "సిస్టమ్",
        "settings_default_reminders" to "డిఫాల్ట్ రిమైండర్‌లు",
        "settings_default_reminders_sub" to "1 రోజు ముందు - 9:00 AM",
        "settings_doc_privacy" to "పత్రం గోప్యత",
        "settings_doc_privacy_sub" to "మీరు పంచుకునే వరకు ప్రైవేట్",
        "settings_data_export" to "డేటా ఎగుమతి",
        "settings_data_export_sub" to "పత్రాలు మరియు పనులు - ZIP ఎగుమతి",
        "settings_info_banner" to "భాగస్వామ్యం చేయడానికి గ్రహీత అనుమతి అవసరం. మీ అనుమతి లేకుండా డేటా నిల్వ చేయబడదు.",
        "settings_account_control" to "ఖాతా నియంత్రణ",
        "settings_delete_account" to "ఖాతాను తొలగించండి",
        "settings_delete_account_desc" to "మీ ప్రొఫైల్, పత్రాలు, పనులు మరియు రిమైండర్‌లను శాశ్వతంగా తొలగించండి.",

        // Language Screen
        "lang_title" to "భాష",
        "lang_choose" to "మీ ప్రాధాన్యత గల భాషను ఎంచుకోండి",

        // Documents & Actions
        "upload_doc_title" to "పత్రాన్ని అప్‌లోడ్ చేయండి",
        "scan_doc_title" to "పత్రాన్ని స్కాన్ చేయండి",
        "doc_preview_title" to "పత్రం మునుజూపు",
        "notice_details_title" to "నోటీసు వివరాలు",
        "review_actions_title" to "సూచించిన చర్యలు",
        "upcoming_actions_screen_title" to "రాబోయే చర్యలు",
        "action_continue" to "కొనసాగించండి",
        "action_back" to "వెనుకకు",

        // Notice Analysis Q&A
        "qa_what_about" to "ఈ పత్రం దేని గురించి?",
        "qa_what_about_sub" to "సరళమైన పదాలలో AI విశ్లేషణ",
        "qa_what_to_do" to "నేను ఏమి చేయాలి?",
        "qa_what_to_do_sub" to "అవసరమైన పనులు మరియు చర్యలు",
        "qa_by_when" to "నేను ఎప్పటిలోగా చేయాలి?",
        "qa_by_when_sub" to "ముఖ్యమైన గడువులు మరియు తేదీలు",
        "qa_extracted_ocr" to "పత్రం వచనం (OCR)",
        "qa_copy_text" to "వచనాన్ని కాపీ చేయండి",
        "qa_review_actions" to "చర్యలను సమీక్షించండి",
        "qa_tasks_identified" to "పనులు గుర్తించబడ్డాయి",
        "qa_primary_deadline" to "ప్రధాన గడువు తేదీ"
    )

    // Hindi (हिन्दी)
    private val hi = mapOf(
        "nav_home" to "होम",
        "nav_tasks" to "कार्य",
        "nav_reminders" to "रिमाइंडर",
        "nav_profile" to "प्रोफ़ाइल",

        // Dashboard
        "dash_category" to "B • मुख्य डैशबोर्ड और नेविगेशन",
        "dash_good_morning" to "शुभ प्रभात",
        "dash_banner_title" to "दस्तावेज़ों को\nकार्यों में बदलें।",
        "dash_banner_sub" to "बिना किसी अनुमान के आपका अगला कदम।",
        "dash_scan_document" to "दस्तावेज़ स्कैन करें",
        "dash_upload_document" to "दस्तावेज़ अपलोड करें",
        "dash_docs_processed" to "प्रसंस्कृत दस्तावेज़",
        "dash_upcoming_actions" to "आगामी कार्य",
        "dash_recent_docs" to "हाल के दस्तावेज़",
        "dash_view_all" to "सभी देखें",
        "dash_upcoming_deadlines" to "आगामी समय सीमा",
        "dash_calendar" to "कैलेंडर",
        "dash_verify_dates" to "तिथियों की पुष्टि करें",
        "dash_actions_identified" to "कार्य पहचाने गए",

        // Navigation Drawer
        "menu_home" to "होम",
        "menu_documents" to "मेरे दस्तावेज़",
        "menu_tasks" to "मेरे कार्य",
        "menu_reminders" to "रिमाइंडर",
        "menu_calendar" to "कैलेंडर",
        "menu_groups" to "समूह",
        "menu_settings" to "सेटिंग्स",
        "menu_help" to "सहायता और समर्थन",
        "menu_logout" to "लॉग आउट",

        // Profile Screen
        "prof_category" to "H • प्रोफ़ाइल, सेटिंग्स और समर्थन",
        "prof_title" to "मेरी प्रोफ़ाइल",
        "prof_edit_profile" to "प्रोफ़ाइल संपादित करें",
        "prof_notifications" to "सूचनाएं",
        "prof_reminders_enabled" to "2 रिमाइंडर सक्षम हैं",
        "prof_language" to "भाषा",
        "prof_account_settings" to "खाता सेटिंग्स",
        "prof_account_settings_sub" to "व्यक्तिगत विवरण और सुरक्षा",
        "prof_privacy_data" to "गोपनीयता और डेटा",
        "prof_privacy_data_sub" to "आपके दस्तावेज़, आपका नियंत्रण",
        "prof_help_support" to "सहायता और समर्थन",
        "prof_help_support_sub" to "मार्गदर्शिकाएँ और संपर्क विकल्प",
        "prof_logout" to "लॉग आउट",

        // Edit Profile Dialog
        "edit_prof_title" to "प्रोफ़ाइल संपादित करें",
        "edit_prof_choose_photo" to "गैलरी से फ़ोटो चुनें",
        "edit_prof_full_name" to "पूरा नाम",
        "edit_prof_enter_name" to "अपना नाम दर्ज करें",
        "edit_prof_email" to "ईमेल आईडी",
        "edit_prof_enter_email" to "अपना ईमेल दर्ज करें",
        "edit_prof_save" to "परिवर्तन सहेजें",
        "edit_prof_saving" to "सहेजा जा रहा है...",
        "edit_prof_cancel" to "रद्द करें",
        "edit_prof_success" to "प्रोफ़ाइल सफलतापूर्वक अपडेट की गई!",

        // Settings & Privacy Screen
        "settings_title" to "सेटिंग्स और गोपनीयता",
        "settings_appearance" to "प्रकटन",
        "settings_light" to "लाइट",
        "settings_dark" to "डार्क",
        "settings_system" to "सिस्टम",
        "settings_default_reminders" to "डिफ़ॉल्ट रिमाइंडर",
        "settings_default_reminders_sub" to "1 दिन पहले - 9:00 AM",
        "settings_doc_privacy" to "दस्तावेज़ गोपनीयता",
        "settings_doc_privacy_sub" to "निजी जब तक आप साझा न करें",
        "settings_data_export" to "डेटा निर्यात",
        "settings_data_export_sub" to "दस्तावेज़ और कार्य - ZIP निर्यात",
        "settings_info_banner" to "साझा करने के लिए प्राप्तकर्ता की अनुमति आवश्यक है। बिना अनुमति कोई डेटा नहीं रखा जाता।",
        "settings_account_control" to "खाता नियंत्रण",
        "settings_delete_account" to "खाता हटाएं",
        "settings_delete_account_desc" to "अपनी प्रोफ़ाइल, दस्तावेज़, कार्य और रिमाइंडर स्थायी रूप से हटाएं।",

        // Language Screen
        "lang_title" to "भाषा",
        "lang_subtitle" to "अपनी पसंदीदा भाषा चुनें",

        // Documents & Actions
        "upload_doc_title" to "दस्तावेज़ अपलोड करें",
        "scan_doc_title" to "दस्तावेज़ स्कैन करें",
        "doc_preview_title" to "दस्तावेज़ पूर्वावलोकन",
        "notice_details_title" to "नोटिस विवरण",
        "review_actions_title" to "सुझाए गए कार्य",
        "upcoming_actions_screen_title" to "आगामी कार्य",
        "action_continue" to "जारी रखें",
        "action_back" to "पीछे",

        // Notice Analysis Q&A
        "qa_what_about" to "यह दस्तावेज़ किस बारे में है?",
        "qa_what_about_sub" to "सरल शब्दों में AI विश्लेषण",
        "qa_what_to_do" to "मुझे क्या करना होगा?",
        "qa_what_to_do_sub" to "आवश्यक कार्य और कदम",
        "qa_by_when" to "मुझे इसे कब तक करना होगा?",
        "qa_by_when_sub" to "महत्वपूर्ण समय सीमा और तारीखें",
        "qa_extracted_ocr" to "निकाला गया पाठ (OCR)",
        "qa_copy_text" to "पाठ कॉपी करें",
        "qa_review_actions" to "कार्यों की समीक्षा करें",
        "qa_tasks_identified" to "कार्य पहचाने गए",
        "qa_primary_deadline" to "मुख्य अंतिम तिथि"
    )

    // Kannada (ಕನ್ನಡ)
    private val kn = mapOf(
        "nav_home" to "ಮುಖಪುಟ",
        "nav_tasks" to "ಕಾರ್ಯಗಳು",
        "nav_reminders" to "ಜ್ಞಾಪನೆಗಳು",
        "nav_profile" to "ಪ್ರೊಫೈಲ್",

        // Dashboard
        "dash_category" to "B • ಮುಖ್ಯ ಡ್ಯಾಶ್‌ಬೋರ್ಡ್ & ನ್ಯಾವಿಗೇಷನ್",
        "dash_good_morning" to "ಶುಭೋದಯ",
        "dash_banner_title" to "ದಾಖಲೆಗಳನ್ನು\nಕ್ರಮಗಳಾಗಿ ಪರಿವರ್ತಿಸಿ.",
        "dash_banner_sub" to "ಊಹೆಯಿಲ್ಲದೆ ನಿಮ್ಮ ಮುಂದಿನ ಹೆಜ್ಜೆ.",
        "dash_scan_document" to "ದಾಖಲೆಯನ್ನು ಸ್ಕ್ಯಾನ್ ಮಾಡಿ",
        "dash_upload_document" to "ದಾಖಲೆಯನ್ನು ಅಪ್‌ಲೋಡ್ ಮಾಡಿ",
        "dash_docs_processed" to "ಪ್ರಕ್ರಿಯೆಗೊಳಿಸಿದ ದಾಖಲೆಗಳು",
        "dash_upcoming_actions" to "ಮುಂಬರುವ ಕ್ರಮಗಳು",
        "dash_recent_docs" to "ಇತ್ತೀಚಿನ ದಾಖಲೆಗಳು",
        "dash_view_all" to "ಎಲ್ಲವನ್ನೂ ವೀಕ್ಷಿಸಿ",
        "dash_upcoming_deadlines" to "ಮುಂಬರುವ ಗಡುವುಗಳು",
        "dash_calendar" to "ಕ್ಯಾಲೆಂಡರ್",
        "dash_verify_dates" to "ದಿನಾಂಕಗಳನ್ನು ಪರಿಶೀಲಿಸಿ",
        "dash_actions_identified" to "ಕ್ರಮಗಳು ಗುರುತಿಸಲಾಗಿದೆ",

        // Navigation Drawer
        "menu_home" to "ಮುಖಪುಟ",
        "menu_documents" to "ನನ್ನ ದಾಖಲೆಗಳು",
        "menu_tasks" to "ನನ್ನ ಕಾರ್ಯಗಳು",
        "menu_reminders" to "ಜ್ಞಾಪನೆಗಳು",
        "menu_calendar" to "ಕ್ಯಾಲೆಂಡರ್",
        "menu_groups" to "ಗುಂಪುಗಳು",
        "menu_settings" to "ಸೆಟ್ಟಿಂಗ್‌ಗಳು",
        "menu_help" to "ಸಹಾಯ ಮತ್ತು ಬೆಂಬಲ",
        "menu_logout" to "ಲಾಗ್ ಔಟ್",

        // Profile Screen
        "prof_category" to "H • ಪ್ರೊಫೈಲ್, ಸೆಟ್ಟಿಂಗ್‌ಗಳು & ಬೆಂಬಲ",
        "prof_title" to "ನನ್ನ ಪ್ರೊಫೈಲ್",
        "prof_edit_profile" to "ಪ್ರೊಫೈಲ್ ಸಂಪಾದಿಸಿ",
        "prof_notifications" to "ಅಧಿಸೂಚನೆಗಳು",
        "prof_reminders_enabled" to "2 ಜ್ಞಾಪನೆಗಳು ಸಕ್ರಿಯವಾಗಿವೆ",
        "prof_language" to "ಭಾಷೆ",
        "prof_account_settings" to "ಖಾತೆ ಸೆಟ್ಟಿಂಗ್‌ಗಳು",
        "prof_account_settings_sub" to "ವೈಯಕ್ತಿಕ ವಿವರಗಳು & ಭದ್ರತೆ",
        "prof_privacy_data" to "ಗೌಪ್ಯತೆ & ಡೇಟಾ",
        "prof_privacy_data_sub" to "ನಿಮ್ಮ ದಾಖಲೆಗಳು, ನಿಮ್ಮ ನಿಯಂತ್ರಣ",
        "prof_help_support" to "ಸಹಾಯ ಮತ್ತು ಬೆಂಬಲ",
        "prof_help_support_sub" to "ಮಾರ್ಗದರ್ಶಿಗಳು & ಸಂಪರ್ಕ ಆಯ್ಕೆಗಳು",
        "prof_logout" to "ಲಾಗ್ ಔಟ್",

        // Edit Profile Dialog
        "edit_prof_title" to "ಪ್ರೊಫೈಲ್ ಸಂಪಾದಿಸಿ",
        "edit_prof_choose_photo" to "ಗ್ಯಾಲರಿಯಿಂದ ಫೋಟೋ ಆಯ್ಕೆಮಾಡಿ",
        "edit_prof_full_name" to "ಪೂರ್ಣ ಹೆಸರು",
        "edit_prof_enter_name" to "ನಿಮ್ಮ ಹೆಸರನ್ನು ನಮೂದಿಸಿ",
        "edit_prof_email" to "ಇಮೇಲ್ ಐಡಿ",
        "edit_prof_enter_email" to "ನಿಮ್ಮ ಇಮೇಲ್ ನಮೂದಿಸಿ",
        "edit_prof_save" to "ಬದಲಾವಣೆಗಳನ್ನು ಉಳಿಸಿ",
        "edit_prof_saving" to "ಉಳಿಸಲಾಗುತ್ತಿದೆ...",
        "edit_prof_cancel" to "ರದ್ದುಮಾಡಿ",
        "edit_prof_success" to "ಪ್ರೊಫೈಲ್ ಯಶಸ್ವಿಯಾಗಿ ನವೀಕರಿಸಲಾಗಿದೆ!",

        // Settings & Privacy Screen
        "settings_title" to "ಸೆಟ್ಟಿಂಗ್‌ಗಳು ಮತ್ತು ಗೌಪ್ಯತೆ",
        "settings_appearance" to "ಗೋಚರತೆ",
        "settings_light" to "ಬೆಳಕು",
        "settings_dark" to "ಕತ್ತಲೆ",
        "settings_system" to "ವ್ಯವಸ್ಥೆ",
        "settings_default_reminders" to "ಡೀಫಾಲ್ಟ್ ಜ್ಞಾಪನೆಗಳು",
        "settings_default_reminders_sub" to "1 ದಿನ ಮೊದಲು - 9:00 AM",
        "settings_doc_privacy" to "ದಾಖಲೆ ಗೌಪ್ಯತೆ",
        "settings_doc_privacy_sub" to "ನೀವು ಹಂಚಿಕೊಳ್ಳುವವರೆಗೆ ಖಾಸಗಿ",
        "settings_data_export" to "ಡೇಟಾ ರಫ್ತು",
        "settings_data_export_sub" to "ದಾಖಲೆಗಳು ಮತ್ತು ಕಾರ್ಯಗಳು - ZIP ರಫ್ತು",
        "settings_info_banner" to "ಹಂಚಿಕೊಳ್ಳಲು ಸ್ವೀಕರಿಸುವವರ ಅನುಮತಿ ಅಗತ್ಯವಿದೆ. ನಿಮ್ಮ ಅನುಮತಿಯಿಲ್ಲದೆ ಡೇಟಾ ಸಂಗ್ರಹಿಸಲಾಗುವುದಿಲ್ಲ.",
        "settings_account_control" to "ಖಾತೆ ನಿಯಂತ್ರಣ",
        "settings_delete_account" to "ಖಾತೆಯನ್ನು ಅಳಿಸಿ",
        "settings_delete_account_desc" to "ನಿಮ್ಮ ಪ್ರೊಫೈಲ್, ದಾಖಲೆಗಳು, ಕಾರ್ಯಗಳನ್ನು ಶಾಶ್ವತವಾಗಿ ತೆಗೆದುಹಾಕಿ.",

        // Language Screen
        "lang_title" to "ಭಾಷೆ",
        "lang_subtitle" to "ನಿಮ್ಮ ಆದ್ಯತೆಯ ಭಾಷೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ",

        // Documents & Actions
        "upload_doc_title" to "ದಾಖಲೆಯನ್ನು ಅಪ್‌ಲೋಡ್ ಮಾಡಿ",
        "scan_doc_title" to "ದಾಖಲೆಯನ್ನು ಸ್ಕ್ಯಾನ್ ಮಾಡಿ",
        "doc_preview_title" to "ದಾಖಲೆ ಮುನ್ನೋಟ",
        "notice_details_title" to "ಸೂಚನೆ ವಿವರಗಳು",
        "review_actions_title" to "ಸೂಚಿಸಲಾದ ಕ್ರಮಗಳು",
        "upcoming_actions_screen_title" to "ಮುಂಬರುವ ಕ್ರಮಗಳು",
        "action_continue" to "ಮುಂದುವರಿಸಿ",
        "action_back" to "ಹಿಂದೆ",

        // Notice Analysis Q&A
        "qa_what_about" to "ಈ ದಾಖಲೆಯು ಯಾವುದರ ಬಗ್ಗೆ?",
        "qa_what_about_sub" to "ಸರಳ ಪದಗಳಲ್ಲಿ AI ವಿಶ್ಲೇಷಣೆ",
        "qa_what_to_do" to "ನಾನು ಏನು ಮಾಡಬೇಕು?",
        "qa_what_to_do_sub" to "ಅಗತ್ಯವಿರುವ ಕಾರ್ಯಗಳು ಮತ್ತು ಕ್ರಮಗಳು",
        "qa_by_when" to "ನಾನು ಇದನ್ನು ಯಾವಾಗ ಮಾಡಬೇಕು?",
        "qa_by_when_sub" to "ಪ್ರಮುಖ ಗಡುವು ಮತ್ತು ದಿನಾಂಕಗಳು",
        "qa_extracted_ocr" to "ದಾಖಲೆ ಪಠ್ಯ (OCR)",
        "qa_copy_text" to "ಪಠ್ಯವನ್ನು ನಕಲಿಸಿ",
        "qa_review_actions" to "ಕ್ರಮಗಳನ್ನು ಪರಿಶೀಲಿಸಿ",
        "qa_tasks_identified" to "ಕಾರ್ಯಗಳು ಪತ್ತೆಯಾಗಿವೆ",
        "qa_primary_deadline" to "ಮುಖ್ಯ ಅಂತಿಮ ದಿನಾಂಕ"
    )

    fun get(key: String): String {
        val langMap = when (LanguageManager.currentLanguage) {
            "ta" -> ta
            "ml" -> ml
            "te" -> te
            "hi" -> hi
            "kn" -> kn
            else -> en
        }
        return langMap[key] ?: en[key] ?: key
    }
}
