# Hubitat Package Notification

A Hubitat Elevation app that extends [Doorbell Display](https://github.com/9r5n76yjmzs-design/hubitat-doorbell-display): it makes Doorbell Display show the door when the doorbell camera detects a package, not only when someone rings.

Doorbell Display reacts to the doorbell's button 1. This app presses that button from Hubitat when a package is detected, which is why a doorbell button push appears when a package arrives. Any other automation that reacts to a ring runs too.

It uses a **UniFi Protect Doorbell** device from [hubitat-unifi-protect](https://github.com/9r5n76yjmzs-design/hubitat-unifi-protect).

> **Status:** early development (0.1.0). Not yet tested on a hub.

## How it works

1. The doorbell camera detects a package. The Doorbell device reports button 2 pushed.
1. The app runs **Push** with button 1 on the same Doorbell device.
1. The Doorbell device reports button 1 pushed, as a press from Hubitat. Automations that react to a ring run.

Details:
- **Nothing happens at the door.** The press is made in Hubitat only: the doorbell doesn't chime and Protect records no ring.
- **Every ring automation runs**, not only Doorbell Display. If an automation should react to real rings only, have it check that the button event is physical; presses from this app are digital.
- **Push with button 2 on the Doorbell device** triggers the app too, so you can test it without a package.
- **How often it fires is decided by the Doorbell device**, which reports each Protect package event once and has an optional ignore time for repeat detections.

## Requirements

- [Doorbell Display](https://github.com/9r5n76yjmzs-design/hubitat-doorbell-display), set up for the same doorbell. Without it the app still presses the button, but nothing shows the door.
- [hubitat-unifi-protect](https://github.com/9r5n76yjmzs-design/hubitat-unifi-protect) controller 0.4.0 and Doorbell 0.2.0 or later, with **Real-time updates** on.
- Package detection switched on for the doorbell camera in Protect.

## Setup

1. Go to **Apps Code** → **New App**, paste [`package-notification.groovy`](package-notification.groovy) and click **Save**.
1. Go to **Apps** → **Add User App** → **Package Notification**.
1. Choose the **Doorbell**: the list shows only UniFi Protect Doorbell devices.
1. Click **Done**.

Install the app again for another doorbell.

## Known limitations (0.1.0)

- **The Doorbell device describes the press as a test press** ("pressed in Hubitat (test)"), because that is how it labels every press made from Hubitat.
- **Hubitat can't pre-select a device**, so the doorbell has to be chosen even when there's only one.

## Security

See [SECURITY.md](SECURITY.md) for what the app can reach and the secure coding rules every change follows.

## License

Released into the public domain under [The Unlicense](LICENSE).
