package com.geecommerce.core.security.ssl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.config.SystemConfig;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;

public enum SSL {
    GET;

    private static final String CACHE_NAME = "gc/ssl-context";

    private static final Logger log = LogManager.getLogger(SSL.class);

    public final SSLContext context(String name) {
        Cache<String, SSLContext> c = cache();

        SSLContext context = c.get(name);

        if (context == null) {
            FileInputStream fis = null;
            BufferedInputStream bis = null;

            try {
                File certFile = locateCertFileForCurrentContext(name);

                if (certFile != null) {
                    fis = new FileInputStream(certFile);
                } else {
                    certFile = locateGlobalCertFile(name);

                    if (certFile != null) {
                        fis = new FileInputStream(certFile);
                    }
                }

                if (fis != null) {
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    bis = new BufferedInputStream(fis);

                    Certificate ca = cf.generateCertificate(bis);

                    // Create a KeyStore containing our trusted CAs
                    String keyStoreType = KeyStore.getDefaultType();
                    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                    keyStore.load(null, null);
                    keyStore.setCertificateEntry("ca", ca);

                    // Create a TrustManager that trusts the CAs in our KeyStore
                    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                    tmf.init(keyStore);

                    // Create an SSLContext that uses our TrustManager
                    context = SSLContext.getInstance("TLS");
                    context.init(null, tmf.getTrustManagers(), null);

                    c.put(name, context);
                } else {
                    // Avoid unnecessary file lookups by caching the default
                    // SSLContext under the specified name.
                    context = SSLContext.getInstance("TLS");
                    c.put(name, context);
                }
            } catch (Throwable t) {
                throw new RuntimeException(t.getMessage(), t);
            } finally {
                IOUtils.closeQuietly(bis);
                IOUtils.closeQuietly(fis);
            }
        }

        return context;
    }

    protected File locateGlobalCertFile(String name) {
        String appCertsPath = SystemConfig.GET.val(SystemConfig.APPLICATION_CERTS_PATH);

        File certFile = new File(appCertsPath, name);

        if (certFile.exists()) {
            if (!certFile.isFile()) {
                if (log.isWarnEnabled())
                    log.warn("The located global certificate '" + certFile.getAbsolutePath()
                        + "' does not appear to be a regular file.");
            } else if (!certFile.canRead()) {
                if (log.isWarnEnabled())
                    log.warn("The located global certificate '" + certFile.getAbsolutePath()
                        + "' does not have the necessary read permission.");
            } else {
                if (log.isDebugEnabled())
                    log.debug("Using global certificate '" + certFile.getAbsolutePath() + "'.");

                return certFile;
            }
        }

        return null;
    }

    protected final File locateCertFileForCurrentContext(String name) {
        ApplicationContext appCtx = App.get().context();

        if (appCtx != null) {
            Merchant m = appCtx.getMerchant();
            Store s = appCtx.getStore();
            View v = appCtx.getView();

            // -------------------------------------------------------------
            // Locate in view directory
            // -------------------------------------------------------------

            if (v != null) {
                File certFile = new File(v.getCertsPath(), name);

                if (certFile.exists()) {
                    if (!certFile.isFile()) {
                        if (log.isWarnEnabled())
                            log.warn("The located view certificate '" + certFile.getAbsolutePath()
                                + "' does not appear to be a regular file.");
                    } else if (!certFile.canRead()) {
                        if (log.isWarnEnabled()) {
                            log.warn("The located view certificate '" + certFile.getAbsolutePath()
                                + "' does not have the necessary read permission.");
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Using view certificate '" + certFile.getAbsolutePath() + "'.");
                        }

                        return certFile;
                    }
                }
            }

            // -------------------------------------------------------------
            // Locate in store directory
            // -------------------------------------------------------------

            if (s != null) {
                File certFile = new File(s.getCertsPath(), name);

                if (certFile.exists()) {
                    if (!certFile.isFile()) {
                        if (log.isWarnEnabled())
                            log.warn("The located store certificate '" + certFile.getAbsolutePath()
                                + "' does not appear to be a regular file.");
                    } else if (!certFile.canRead()) {
                        if (log.isWarnEnabled()) {
                            log.warn("The located store certificate '" + certFile.getAbsolutePath()
                                + "' does not have the necessary read permission.");
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Using store certificate '" + certFile.getAbsolutePath() + "'.");
                        }

                        return certFile;
                    }
                }
            }

            // -------------------------------------------------------------
            // Locate in merchant directory
            // -------------------------------------------------------------

            if (m != null) {
                File certFile = new File(m.getCertsPath(), name);

                if (certFile.exists()) {
                    if (!certFile.isFile()) {
                        if (log.isWarnEnabled())
                            log.warn("The located merchant certificate '" + certFile.getAbsolutePath()
                                + "' does not appear to be a regular file.");
                    } else if (!certFile.canRead()) {
                        if (log.isWarnEnabled())
                            log.warn("The located merchant certificate '" + certFile.getAbsolutePath()
                                + "' does not have the necessary read permission.");
                    } else {
                        if (log.isDebugEnabled())
                            log.debug("Using merchant certificate '" + certFile.getAbsolutePath() + "'.");

                        return certFile;
                    }
                }
            }
        }

        return null;
    }

    protected static <T> Cache<String, T> cache() {
        return App.get().inject(CacheManager.class).getCache(CACHE_NAME);
    }
}
