# Security

This app runs on a Hubitat Elevation hub and extends the [Doorbell Display](https://github.com/9r5n76yjmzs-design/hubitat-doorbell-display) app. It only uses the device you select: a UniFi Protect Doorbell from [hubitat-unifi-protect](https://github.com/9r5n76yjmzs-design/hubitat-unifi-protect). The goal is that installing it does not widen what an attacker can reach through the hub.

## Reporting a problem

Please report security problems privately, not in a public issue: on GitHub, open this repository's **Security** tab and choose **Report a vulnerability**. The report goes only to the maintainer, Michael Dailous.

## What the app can reach

- **Only the doorbell you select.** The app makes no network requests of its own, holds no secrets, keeps nothing in `state`, and opens no endpoints (no `mappings`, OAuth or cloud callbacks).
- **What it does:** when the selected Doorbell device reports button 2 pushed (a package), it runs Push with button 1 on that same device. It sends no other command. The press it sends is button 1, which it ignores, so it can't trigger itself.
- **What it reads:** the selected device's button events and its number of buttons. Device values are treated as untrusted text.
- **Anything that can press button 2 on the Doorbell device can make the app press button 1.** That is the app's purpose; it grants nothing a direct press of button 1 would not.

## Secure coding standard

Every change follows these rules. A line may opt out of one checked rule only with a reviewed marker that gives the reason, for example `// security-check: allow S1 (reason)`. Rules marked *(checked)* are enforced by [`.github/scripts/security-check.sh`](.github/scripts/security-check.sh), which runs on every pull request with the Groovy parse check.

1. **S1 (checked):** Secrets are never stored in `state`.
2. **S2 (checked):** Secret values are never written to the logs.
3. **S3 (checked):** Raw inbound messages are never logged.
4. **S4 (checked):** No credentials are hard-coded.
5. **S5 (checked):** Pages never load remote resources (images, scripts, styles).
6. **S6 (checked):** Links that open a new tab use `rel="noopener noreferrer"`.
7. **S7 (checked):** Debug logging is off by default, and turns itself off after 30 minutes when switched on.
8. **S8 (checked):** Any file that skips certificate checks explains why and points here.
9. **S9 (checked):** Continuity records, keys and `.env` files are never committed.
10. **Validate all input** from settings and devices before use; device values are treated as untrusted text.
11. **Least privilege:** use only the devices the user selects; no network access, no endpoints.
12. **Bound everything:** timers and retries have limits.
13. **No new attack surface:** no listening ports, OAuth/cloud endpoints, or remote code or content loading.
