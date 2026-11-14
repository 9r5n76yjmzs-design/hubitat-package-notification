/**
 * Package Notification
 *
 * An extension to the Doorbell Display app (https://github.com/9r5n76yjmzs-design/hubitat-doorbell-display):
 * it makes Doorbell Display show the door when the doorbell camera detects a package, not only on a ring.
 *
 * Doorbell Display reacts to the doorbell's button 1. The UniFi Protect Doorbell device reports a package as
 * button 2 pushed, so this app answers by running Push with button 1 on the same device. That press is why
 * the doorbell button is pushed when a package arrives. Any other automation that reacts to a ring runs too.
 *
 * Uses the UniFi Protect Doorbell driver (0.2.0 or later) from
 * https://github.com/9r5n76yjmzs-design/hubitat-unifi-protect
 *
 * Each installed copy of the app is one automation; install it again for another doorbell.
 *
 * @see https://github.com/9r5n76yjmzs-design/hubitat-package-notification
 */
import groovy.transform.Field

@Field static final String APP_NAME = 'Package Notification'
@Field static final String APP_VERSION = '0.1.0'
// Buttons of the UniFi Protect Doorbell device: 1 is a ring, 2 is a package detection (Doorbell 0.2.0 or later).
@Field static final Integer RING_BUTTON = 1
@Field static final Integer PACKAGE_BUTTON = 2

definition(
    name: APP_NAME,
    namespace: 'dailous',
    author: 'Michael Dailous',
    description: 'An extension to the Doorbell Display app: presses the doorbell button from Hubitat when the doorbell camera detects a package, so Doorbell Display shows the door for a package as it does for a ring.',
    category: 'Convenience',
    iconUrl: '',
    iconX2Url: '',
    importUrl: 'https://raw.githubusercontent.com/9r5n76yjmzs-design/hubitat-package-notification/main/package-notification.groovy',
    singleInstance: false
)

preferences {
    page(name: 'mainPage')
}

Map mainPage() {
    dynamicPage(name: 'mainPage', title: APP_NAME, install: true, uninstall: true) {
        section {
            label title: 'Name for this automation', required: false
        }
        section('Doorbell') {
            input 'doorbell', 'device.UniFiProtectDoorbell', title: 'Doorbell', required: true, submitOnChange: true
            if (doorbell && !hasPackageButton(doorbell)) {
                paragraph "${doorbell.displayName} doesn't report a package button yet. Update the UniFi Protect Doorbell driver to 0.2.0 or later, then run Refresh on the UniFi Protect controller."
            }
            paragraph 'This app is an extension to the Doorbell Display app. When this doorbell reports a package (button 2 pushed), ' +
                'the app runs Push with button 1 on it, so Doorbell Display shows the door. ' +
                'That is a press from Hubitat, not a real ring: nothing happens at the door, but every automation that reacts to button 1 runs.'
        }
        section('Logging') {
            input 'logEnable', 'bool', title: 'Enable debug logging (turns off after 30 minutes)', defaultValue: false
            input 'txtEnable', 'bool', title: 'Enable description text logging', defaultValue: true
        }
    }
}

// ===================================================================================================================
// Lifecycle
// ===================================================================================================================

void installed() {
    log.info "${app.label ?: APP_NAME}: Installed ${APP_NAME} ${APP_VERSION}"
    initialize()
}

void updated() {
    log.info "${app.label ?: APP_NAME}: Settings saved"
    unsubscribe()
    unschedule('logsOff')
    if (logEnable) runIn 1800, 'logsOff'
    initialize()
}

// Debug logging switches itself off after 30 minutes, so it isn't left on by accident.
void logsOff() {
    app.updateSetting 'logEnable', [value:'false', type:'bool']
    log.info "${app.label ?: APP_NAME}: Debug logging disabled"
}

void initialize() {
    subscribe(doorbell, 'pushed', 'buttonHandler')
}

// ===================================================================================================================
// Package detection
// ===================================================================================================================

// Handles the Doorbell device's button events. Only button 2 (a package) is acted on. The press this app
// sends is button 1, which is ignored here, so the app can't trigger itself.
void buttonHandler(evt) {
    String button = "${evt.value}"
    if (button != "${PACKAGE_BUTTON}") {
        log_debug "Button ${button} pushed; not a package, nothing to do"
        return
    }
    if (!doorbell) {
        log.warn "${app.label ?: APP_NAME}: a package was detected, but no doorbell is chosen; open the app and choose one"
        return
    }
    log_info "${doorbell.displayName} saw a package: pushing its button ${RING_BUTTON} so Doorbell Display shows the door"
    doorbell.push(RING_BUTTON)
}

// ===================================================================================================================
// Helpers
// ===================================================================================================================

private static boolean hasPackageButton(device) {
    String buttons = "${device.currentValue('numberOfButtons', true)}"
    return buttons.isInteger() && (buttons as Integer) >= PACKAGE_BUTTON
}

private void log_debug(String message) {
    if (logEnable) log.debug "${app.label ?: APP_NAME}: ${message}"
}

private void log_info(String message) {
    if (txtEnable) log.info "${app.label ?: APP_NAME}: ${message}"
}
