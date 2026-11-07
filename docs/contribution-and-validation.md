# SOFT2412 Contribution and Validation

Virtual Scroll Access System was a Java team project in the University of
Sydney unit SOFT2412 Agile Software Development Practices. My implementation
work centred on catalogue browsing and on connecting that flow to storage and
download behaviour.

## Contribution by sprint

| Sprint | Role | Individual contribution |
| --- | --- | --- |
| 1 | Product Owner and developer | Implemented catalogue and guest browsing, connected the first application flow, and wrote flow tests. Recorded client feedback for the backlog. |
| 2 | Developer | Added filters by uploader, file ID, name and date, plus bounded preview. Helped integrate the browse flow with the other modules. |
| 3 | Scrum Master and developer | Implemented download path retry and cancellation in the interface. Maintained the regression and field-traceability tables and facilitated team coordination. |

## Representative regression cases

| Behaviour | Evidence to check | Public example |
| --- | --- | --- |
| Optional filters combine using AND | A matching ID and uploader still produce no row if the requested date differs. | `RegressionChecks` tests the matching query and a conflicting date. |
| Preview is bounded | A long file shows only its first 64 bytes. A short file shows the available bytes. | The regression compares bytes for both input lengths. |
| Invalid destinations can be retried | A pre-existing file is preserved. A missing parent or a path inside managed storage is rejected. | The scripted input submits all three and checks both messages and file state. |
| Cancellation and successful copy have distinct effects | Cancellation preserves the count. A valid retry produces an exact copy and increments the count once. | The regression checks the count and output bytes. |

The original v1.0.1 group release had 28 applicable offline regression checks
recorded as passed. That figure describes the original team release. The public
example uses a fresh fixture and provides eight independent checks for the
behaviours illustrated here.
