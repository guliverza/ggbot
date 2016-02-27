# Example Play TLS Application

This application shows how to use Play with SSL/TLS, using the Java Secure Socket Extension (JSSE) API.
 
## Requirements

You must have JDK 1.8 installed on your machine to run this, to take advantage of the new [security enhancements in JSSE](http://blog.ivanristic.com/2014/03/ssl-tls-improvements-in-java-8.html).

## Generate Certificates

To use HTTPS, you must have X.509 certificates.  Generating certificates can be painful, so all the scripts needed to generate the certificates needed are included in the `certs` directory.  For more detail, you can see the [Certificate Generation](http://www.playframework.com/documentation/2.3.x/CertificateGeneration) section in Play WS SSL.

To generate certificates, run:

```
cd scripts/
./gencerts.sh
```

and move all of the generated files into the `certs` directory:

```
cd scripts
mkdir ../certs
mv client* ../certs
mv example* ../certs
mv password ../certs
```

## Point example.com to localhost

You may have noticed that the name on the generated certificates is `example.com` -- HTTPS requires that you have a reasonable hostname for your server.

Rather than setting up a DNS entry or a remote server, we'll modify `/etc/hosts` to point to the local directory.

```
$ sudo /etc/hosts
```

```
127.0.0.1       example.com www.example.com
```

## Run Play with HTTPS configuration

Now that you've generated the certificates and added `example.com` to `/etc/hosts`, you can start Play itself.

This application is not run with `activator` -- you should run it with `./play` instead, as there are a number of system properties required to use it effectively.

[Configuring HTTPS](http://www.playframework.com/documentation/2.3.x/ConfiguringHttps)

```
./play run
```

## Checking the list of cipher suites:

Download SSLyze:

[https://github.com/iSECPartners/sslyze/releases](https://github.com/iSECPartners/sslyze/releases)

And then run SSLyze against the play application:

```
cd sslyze-0_9-osx64
python sslyze.py --regular www.example.com:9443
```

You should see results like:

```
 REGISTERING AVAILABLE PLUGINS
 -----------------------------

  PluginOpenSSLCipherSuites
  PluginCertInfo
  PluginCompression
  PluginHSTS
  PluginHeartbleed
  PluginSessionRenegotiation
  PluginSessionResumption



 CHECKING HOST(S) AVAILABILITY
 -----------------------------

   www.example.com:9443                => 127.0.0.1:9443



 SCAN RESULTS FOR WWW.EXAMPLE.COM:9443 - 127.0.0.1:9443
 ------------------------------------------------------

  * Session Renegotiation:
      Client-initiated Renegotiations:   Rejected
      Secure Renegotiation:              Supported

  * Compression:
      DEFLATE Compression:               Disabled

  * Heartbleed:
      OpenSSL Heartbleed:                NOT vulnerable

Unhandled exception when processing --certinfo:
exceptions.KeyError - 'exponent'

  * Session Resumption:
      With Session IDs:                  Not supported (0 successful, 5 failed, 0 errors, 5 total attempts).
      With TLS Session Tickets:          Not Supported - TLS ticket not assigned.

  * SSLV2 Cipher Suites:
      Server rejected all cipher suites.

  * TLSV1_2 Cipher Suites:
      Preferred:
                 ECDHE-ECDSA-AES256-SHA384     256 bits      HTTP 200 OK
      Accepted:
                 ECDHE-ECDSA-AES256-SHA384     256 bits      HTTP 200 OK
                 ECDHE-ECDSA-AES256-SHA        256 bits      HTTP 200 OK
                 ECDHE-ECDSA-AES256-GCM-SHA384 256 bits      HTTP 200 OK
                 ECDHE-ECDSA-DES-CBC3-SHA      168 bits      HTTP 200 OK
                 ECDHE-ECDSA-RC4-SHA           128 bits      HTTP 200 OK
                 ECDHE-ECDSA-AES128-SHA256     128 bits      HTTP 200 OK
                 ECDHE-ECDSA-AES128-SHA        128 bits      HTTP 200 OK
                 ECDHE-ECDSA-AES128-GCM-SHA256 128 bits      HTTP 200 OK

  * TLSV1_1 Cipher Suites:
      Preferred:
                 ECDHE-ECDSA-AES256-SHA        256 bits      HTTP 200 OK
      Accepted:
                 ECDHE-ECDSA-AES256-SHA        256 bits      HTTP 200 OK
                 ECDHE-ECDSA-DES-CBC3-SHA      168 bits      HTTP 200 OK
                 ECDHE-ECDSA-RC4-SHA           128 bits      HTTP 200 OK
                 ECDHE-ECDSA-AES128-SHA        128 bits      HTTP 200 OK

  * TLSV1 Cipher Suites:
      Preferred:
                 ECDHE-ECDSA-AES256-SHA        256 bits      HTTP 200 OK
      Accepted:
                 ECDHE-ECDSA-AES256-SHA        256 bits      HTTP 200 OK
                 ECDHE-ECDSA-DES-CBC3-SHA      168 bits      HTTP 200 OK
                 ECDHE-ECDSA-RC4-SHA           128 bits      HTTP 200 OK
                 ECDHE-ECDSA-AES128-SHA        128 bits      HTTP 200 OK

  * SSLV3 Cipher Suites:
      Preferred:
                 ECDHE-ECDSA-AES256-SHA        256 bits      HTTP 200 OK
      Accepted:
                 ECDHE-ECDSA-AES256-SHA        256 bits      HTTP 200 OK
                 ECDHE-ECDSA-DES-CBC3-SHA      168 bits      HTTP 200 OK
                 ECDHE-ECDSA-RC4-SHA           128 bits      HTTP 200 OK
                 ECDHE-ECDSA-AES128-SHA        128 bits      HTTP 200 OK



 SCAN COMPLETED IN 9.51 S
 ------------------------
 ```

## Turning on Client Authentication

Now that you've verified that the server is running and can speak HTTPS, go into `./play` and change the `play.ssl.needClientAuth` setting from `false` to `true`.  Then restart the server.  You should see

```
   ECDHE-ECDSA-RC4-SHA             ClientCertificateRequested - Server requested a client certificate issued by one of the following CAs: '/C=US/ST=California/L=San Francisco/O=Example Company/OU=Example Org/CN=clientca'.
```

Now that the server requires client authentication, a client must now provide a certificate signed by the `clientca` root certificate before a connection can be established.

## Connecting to the server with Play WS

Fortunately, we happen to have [Play WS](http://www.playframework.com/documentation/2.3.x/ScalaWS), an HTTP client library that can use [TLS client authentication](http://www.playframework.com/documentation/2.3.x/WsSSL).

The `ws.conf` script looks like this:

```
ws.ssl {

  protocol = "TLSv1.2"

  enabledProtocols = [ "TLSv1.2" ]

  enabledCiphers = [
    "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384"
  ]

  ws.ssl.disabledSignatureAlgorithms = "MD2, MD4, MD5, SHA1, RSA"

  ws.ssl.disabledKeyAlgorithms = "EC keySize < 384"

  keyManager = {
    stores = [
      // Note: app must be run from ./play, which loads the KEY_PASSWORD environment variable.
      { type: "JKS", path: "certs/client.jks", password: ${?KEY_PASSWORD} },
    ]
  }

  trustManager = {
    stores = [
      { type = "JKS", path = "certs/exampletrust.jks" }
    ]
  }
}

```

Normally you would use [Play WS](http://www.playframework.com/documentation/2.3.x/ScalaWS) in the context of a Play application, but it can also be run directly from `Main`.

Open up a new shell, and type:

```
$ ./play
> runMain Main
```

You should see:

```
[info] Running Main
header = (Content-Length,Buffer(106))
header = (Content-Type,Buffer(text/html; charset=utf-8))
body =
<!DOCTYPE html>
<html>
 <body>
   <h1>Congratulations!  You are reading the page!</h1>
 </body>
</html>
```

Now, to verify that it's only working because of the client's key, comment out the keyManager section in `ws.conf` and rerun `Main` -- you will see that the WS client fails client authentication:

```
failure = java.net.ConnectException: Received fatal alert: bad_certificate to https://example.com:9443/
```

That's it.# ggbot
