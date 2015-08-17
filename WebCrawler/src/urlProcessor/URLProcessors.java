package urlProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class URLProcessors {
	private static final Map<String, IURLProcessorProvider> m_providers = new ConcurrentHashMap<String, IURLProcessorProvider>();
	
	private URLProcessors() {
	}
	
	public static void RegisterURLProcessorProvider(String name, IURLProcessorProvider provider) {
		m_providers.put(name, provider);
	}
	
	public static IURLProcessor getURLProcessor(String name) {
		IURLProcessorProvider processorProvider = m_providers.get(name);
		
		if (processorProvider == null) {
			throw new IllegalArgumentException("No url processor with given name");
		}
		
		return processorProvider.newURLProcessor();
	}
}
