# OrderFlow — Legacy Java EE to Kubernetes Migration

A hands-on simulation of migrating a legacy multi-module Jakarta EE
application (WildFly + Oracle-style stack) to Kubernetes, built to
demonstrate real-world DevOps patterns rather than a toy tutorial app.

## Why This Project Exists
Enterprise Java EE applications often run multiple WARs on a single
shared application server. This creates operational risks: classloader
conflicts between library versions, cascading failures when a shared
database is unavailable, and brittle CI/CD health checks that report
false positives.

This project investigates one such conflict (a Log4j 1.x vs 2.x
classloader clash under WildFly's shared classloading model),
documents the root cause, and demonstrates how Kubernetes's per-pod
isolation architecture eliminates this entire category of problem —
along with other production-hardening patterns: DB-readiness init
containers, real health-check-based probes, StatefulSets for stateful
services, and GitOps-based CI/CD.

## What's Inside
- `order-service/` — Jakarta EE WAR (Log4j 1.x)
- `task-service/` — Jakarta EE WAR (Log4j 2.x)
- `docs/` — investigation notes and architecture decisions
- `k8s-manifests/` — Kubernetes deployment configuration
- `.github/workflows/` — CI/CD pipeline
