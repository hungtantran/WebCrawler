package urlPrioritizer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class URLPrioritizers {
    private static final Map<String, IURLPrioritizerProvider> m_providers = new ConcurrentHashMap<>();

    private URLPrioritizers() {
    }

    public static void RegisterURLPrioritizerProvider(String name, IURLPrioritizerProvider prioritizer) {
        m_providers.put(name, prioritizer);
    }

    public static IURLPrioritizer getURLPrioritizer(String name) {
        IURLPrioritizerProvider prioritizerProvider = m_providers.get(name);

        if (prioritizerProvider == null) {
            throw new IllegalArgumentException("No prioritizer with given name");
        }

        return prioritizerProvider.newURLPrioritizer();
    }
}
