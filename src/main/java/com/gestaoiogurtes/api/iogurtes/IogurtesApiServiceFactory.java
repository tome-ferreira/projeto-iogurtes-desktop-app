package com.gestaoiogurtes.api.iogurtes;

/**
 * Factory that controls which API implementation is used throughout the app.
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │  To switch from mock → real API, change ONE line here:      │
 * │                                                             │
 * │    static final boolean USE_MOCK = false;                   │
 * └─────────────────────────────────────────────────────────────┘
 */
public class IogurtesApiServiceFactory {

    // ← Change this to false to use the real API
    private static final boolean USE_MOCK = true;

    private static IIogurtesApiService instance;

    public static IIogurtesApiService getInstance() {
        if (instance == null) {
            instance = USE_MOCK
                    ? new MockIogurtesApiService()
                    : new RealIogurtesApiService();
        }
        return instance;
    }

    private IogurtesApiServiceFactory() {}
}
