package urlFilter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class URLFilters {
	private static final Map<String, IURLFilterProvider> m_providers = new ConcurrentHashMap<String, IURLFilterProvider>();
	
	private URLFilters() {
	}
	
	public static void RegisterURLFilterProvider(String name, IURLFilterProvider provider) {
		m_providers.put(name, provider);
	}
	
	public static IURLFilter getURLFilter(String name) {
		IURLFilterProvider filterProvider = m_providers.get(name);
		
		if (filterProvider == null) {
			throw new IllegalArgumentException("No filter with given name");
		}
		
		return filterProvider.newURLFilter();
	}
}
