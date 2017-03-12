import java.io.IOException;

public class MyTTSEngine {

	private SPEECH_LANGUAGE defaultLanguage = SPEECH_LANGUAGE.ENGLISH;

	public MyTTSEngine() {
		// TODO Auto-generated constructor stub
	}
	
	public MyTTSEngine(SPEECH_LANGUAGE lang) {
		defaultLanguage = lang;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MyTTSEngine ttsEngine = new MyTTSEngine();
		ttsEngine.speak("I can speak English");
		ttsEngine.speak("我也会说普通话。", SPEECH_LANGUAGE.MANDARIN);
		ttsEngine.speak("我还会说广东话~", SPEECH_LANGUAGE.CANTONESE);
	}

	public static enum SPEECH_LANGUAGE {
		ENGLISH, MANDARIN, CANTONESE
	}

	public void speak(String text) {
		speak(text, defaultLanguage);
	}

	public void speak(String text, SPEECH_LANGUAGE lang) {
		try {
			Process p = Runtime.getRuntime()
					.exec(new String[] { "say", "-v",
							getVoiceForLanguage(lang), text });
			p.waitFor();
			System.out.println(p.exitValue());
			p.destroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println("The operating system does not have a 'say' command. Try FreeTTS.");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public SPEECH_LANGUAGE getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(SPEECH_LANGUAGE defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	private String getVoiceForLanguage(SPEECH_LANGUAGE lang) {
		if (lang.equals(SPEECH_LANGUAGE.MANDARIN))
			return "Ting-Ting";
		if (lang.equals(SPEECH_LANGUAGE.CANTONESE))
			return "Sin-Ji";
		return "Alex";
	}
}
