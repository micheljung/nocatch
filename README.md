# NoCatch
Get rid of annoying checked exceptions!

## Usage
Instead of:
```java
try {
  URL url = new URL("http://www.github.com");
  // Other code you really don't want in this try-block
} catch (MalformedURLException e) {
  throw new RuntimeException(e);
}
```
(or even worse, propagating the exception up your stack)

Just write:
```java
import static com.github.nocatch.NoCatch.noCatch;
...
URL url = noCatch(() -> new URL("http://www.github.com"));
```

And any checked exception will automatically be wrapped into a runtime exception (`NoCatchException`). Woa!

## Dependency

Gradle:

```groovy
dependencies {
  compile 'io.github.micheljung:nocatch:2.0'
}
```

### Custom wrapper exception

You can specify your own wrapper exception just like so:
```java
// Throws WrapperException instead of NoCatchException
URL url = noCatch(() -> new URL(";"), WrapperException.class);
```
Make sure your exception has a contructor like `public WrapperException(Throwable cause)`.

### Global custom wrapper exception

Want to specify your own wrapper exception globally? No problem:
```java
NoCatch.setDefaultWrapperException(RuntimeException.class);

// Throws RuntimeException
noCatch(() -> new URL(";"));
```
