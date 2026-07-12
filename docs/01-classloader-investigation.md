# Classloader Conflict Investigation

## Hypothesis
Two WARs (order-service using Log4j 1.x, task-service using Log4j 2.x)
deployed to the same WildFly instance would produce a NoSuchMethodError,
mirroring a production classloader conflict pattern.

## Test 1: Plain WAR Deployment
Deployed order-service.war and task-service.war independently (not
inside an EAR) to the same WildFly 39 instance.

**Result:** No conflict. Both endpoints returned successfully.

## Root Cause
WildFly assigns each WAR its own isolated classloader by default.
Since Log4j 1.x and Log4j 2.x were bundled inside each WAR's own
WEB-INF/lib, neither app's classloader could see the other's Log4j
version. This is standard Jakarta EE modular classloading behavior.

## Conclusion
Plain per-WAR deployment does NOT reproduce this class of bug. Real
production classloader conflicts of this type require one of:
1. Both WARs packaged in a shared EAR, where library JARs placed in
   the EAR's lib/ directory become visible to all contained WARs.
2. Both WARs declaring a dependency on a shared WildFly global module
   (installed once under JBOSS_HOME/modules) via
   jboss-deployment-structure.xml.

This finding itself demonstrates WildFly's classloading isolation
model, and clarifies that the conflict pattern (as seen in production)
depends specifically on shared-library packaging decisions -- not
merely running multiple WARs on one server.

## Why This Still Matters for the K8s Migration
Regardless of which shared-classloader mechanism causes the conflict,
the Kubernetes fix is the same: per-service pod isolation gives each
app its own JVM and classpath, eliminating the shared-classloader
attack surface entirely -- whether the conflict originates from an
EAR, a global module, or any future shared dependency.
