package uk.nhs.nhsx.sonar.android.app.status

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.junit.Test
import uk.nhs.nhsx.sonar.android.app.testhelpers.base.EspressoTest
import uk.nhs.nhsx.sonar.android.app.R
import uk.nhs.nhsx.sonar.android.app.inbox.TestInfo
import uk.nhs.nhsx.sonar.android.app.inbox.TestResult
import uk.nhs.nhsx.sonar.android.app.testhelpers.TestData
import uk.nhs.nhsx.sonar.android.app.testhelpers.robots.BottomDialogRobot
import uk.nhs.nhsx.sonar.android.app.testhelpers.robots.StatusRobot

class StatusActivityTest : EspressoTest() {

    private val statusRobot = StatusRobot()
    private val bottomDialogRobot = BottomDialogRobot()
    private val testData = TestData()

    private fun showsTestResultDialogOnResume(testResult: TestResult, state: UserState) {
        testAppContext.addTestInfo(TestInfo(testResult, DateTime.now()))

        startStatusActivityWith(state)

        bottomDialogRobot.checkTestResultDialogIsDisplayed(testResult)
        bottomDialogRobot.clickSecondCtaButton()
        bottomDialogRobot.checkBottomDialogIsNotDisplayed()
    }

    @Test
    fun showsDefaultStateWhenRegistrationNotFinished() {
        testAppContext.setFinishedOnboarding()
        testAppContext.app.startTestActivity<StatusActivity>()

        statusRobot.checkFinalisingSetup()
        statusRobot.checkActivityIsDisplayed(DefaultState::class)
        statusRobot.checkStatusDescriptionIsNotDisplayed()

        statusRobot.checkCurrentAdviceCardIsDisplayed()
        statusRobot.checkFeelUnwellIsDisabled()
        statusRobot.checkBookVirusTestCardIsNotDisplayed()

        statusRobot.swipeToBottom()

        statusRobot.checkInformationAboutTestingIsDisplayed()
        statusRobot.checkWorkplaceGuidanceIsDisplayed()
        statusRobot.checkNhsServicesLinkIsDisplayed()
    }

    @Test
    fun showsDefaultState() {
        startStatusActivityWith(testData.defaultState)

        statusRobot.checkAppIsWorking()
        statusRobot.checkActivityIsDisplayed(DefaultState::class)
        statusRobot.checkStatusDescriptionIsNotDisplayed()

        statusRobot.checkCurrentAdviceCardIsDisplayed()
        statusRobot.checkFeelUnwellIsDisplayed()
        statusRobot.checkBookVirusTestCardIsNotDisplayed()

        statusRobot.swipeToBottom()

        statusRobot.checkInformationAboutTestingIsDisplayed()
        statusRobot.checkWorkplaceGuidanceIsDisplayed()
        statusRobot.checkNhsServicesLinkIsDisplayed()
    }

    @Test
    fun showsExposedState() {
        startStatusActivityWith(testData.exposedState)

        statusRobot.checkAppIsWorking()
        statusRobot.checkActivityIsDisplayed(ExposedState::class)
        statusRobot.checkStatusDescription(testData.exposedState)

        statusRobot.checkCurrentAdviceCardIsDisplayed()
        statusRobot.checkFeelUnwellIsDisplayed()
        statusRobot.checkBookVirusTestCardIsNotDisplayed()

        statusRobot.swipeToBottom()

        statusRobot.checkInformationAboutTestingIsDisplayed()
        statusRobot.checkWorkplaceGuidanceIsDisplayed()
        statusRobot.checkNhsServicesLinkIsDisplayed()
    }

    @Test
    fun showsSymptomaticState() {
        startStatusActivityWith(testData.symptomaticState)

        statusRobot.checkAppIsWorking()
        statusRobot.checkActivityIsDisplayed(SymptomaticState::class)
        statusRobot.checkStatusDescription(testData.symptomaticState)

        statusRobot.checkCurrentAdviceCardIsDisplayed()
        statusRobot.checkFeelUnwellIsNotDisplayed()
        statusRobot.checkBookVirusTestCardIsDisplayed()

        statusRobot.swipeToBottom()

        statusRobot.checkInformationAboutTestingIsDisplayed()
        statusRobot.checkWorkplaceGuidanceIsDisplayed()
        statusRobot.checkNhsServicesLinkIsDisplayed()
    }

    @Test
    fun showsExposedSymptomaticState() {
        startStatusActivityWith(testData.exposedSymptomaticState)

        statusRobot.checkAppIsWorking()
        statusRobot.checkActivityIsDisplayed(ExposedSymptomaticState::class)
        statusRobot.checkStatusDescription(testData.exposedSymptomaticState)

        statusRobot.checkCurrentAdviceCardIsDisplayed()
        statusRobot.checkFeelUnwellIsNotDisplayed()
        statusRobot.checkBookVirusTestCardIsDisplayed()

        statusRobot.swipeToBottom()

        statusRobot.checkInformationAboutTestingIsDisplayed()
        statusRobot.checkWorkplaceGuidanceIsDisplayed()
        statusRobot.checkNhsServicesLinkIsDisplayed()
    }

    @Test
    fun showsPositiveState() {
        startStatusActivityWith(testData.positiveState)

        statusRobot.checkAppIsWorking()
        statusRobot.checkActivityIsDisplayed(PositiveState::class)
        statusRobot.checkStatusDescription(testData.positiveState)

        statusRobot.checkCurrentAdviceCardIsDisplayed()
        statusRobot.checkFeelUnwellIsNotDisplayed()
        statusRobot.checkBookVirusTestCardIsNotDisplayed()

        statusRobot.swipeToBottom()

        statusRobot.checkInformationAboutTestingIsDisplayed()
        statusRobot.checkWorkplaceGuidanceIsDisplayed()
        statusRobot.checkNhsServicesLinkIsDisplayed()
    }

    @Test
    fun registrationRetry() {
        testAppContext.setFinishedOnboarding()
        testAppContext.simulateBackendResponse(error = true)

        testAppContext.app.startTestActivity<StatusActivity>()
        statusRobot.checkFinalisingSetup()

        testAppContext.simulateBackendResponse(error = false)
        testAppContext.verifyRegistrationRetry()

        statusRobot.waitForRegistrationToComplete()
        statusRobot.checkFeelUnwellIsDisplayed()
    }

    @Test
    fun registrationPushNotificationNotReceived() {
        testAppContext.setFinishedOnboarding()
        testAppContext.simulateBackendDelay(400)

        testAppContext.app.startTestActivity<StatusActivity>()
        statusRobot.checkFinalisingSetup()

        testAppContext.verifyReceivedRegistrationRequest()
        testAppContext.verifyRegistrationFlow()

        statusRobot.waitForRegistrationToComplete()
        statusRobot.checkFeelUnwellIsDisplayed()
    }

    @Test
    fun showsRecoveryDialogOnResume() {
        testAppContext.setFullValidUser(testData.defaultState)
        testAppContext.addRecoveryMessage()

        testAppContext.app.startTestActivity<StatusActivity>()

        bottomDialogRobot.checkRecoveryDialogIsDisplayed()
        bottomDialogRobot.clickSecondCtaButton()
        bottomDialogRobot.checkBottomDialogIsNotDisplayed()
    }

    @Test
    fun bottomDialogWhenStateIsExpiredSelectingUpdatingSymptoms() {
        startStatusActivityWith(testData.expiredSymptomaticState)

        bottomDialogRobot.checkUpdateSymptomsDialogIsDisplayed()
        bottomDialogRobot.clickFirstCtaButton()
        bottomDialogRobot.checkBottomDialogIsNotDisplayed()
    }

    @Test
    fun bottomDialogWhenStateIsExpiredSelectingNoSymptoms() {
        startStatusActivityWith(testData.expiredSymptomaticState)

        bottomDialogRobot.checkUpdateSymptomsDialogIsDisplayed()
        bottomDialogRobot.clickSecondCtaButton()
        bottomDialogRobot.checkBottomDialogIsNotDisplayed()
    }

    @Test
    fun showsUpdateSymptomsDialogWhenPositiveStateExpired() {
        startStatusActivityWith(testData.expiredPositiveState)

        bottomDialogRobot.checkUpdateSymptomsDialogIsDisplayed()
        bottomDialogRobot.clickSecondCtaButton()
        bottomDialogRobot.checkBottomDialogIsNotDisplayed()
    }

    @Test
    fun showsPositiveTestResultDialogOnResumeForDefaultState() {
        showsTestResultDialogOnResume(TestResult.POSITIVE, DefaultState)
    }

    @Test
    fun showsNegativeTestResultDialogOnResumeForDefaultState() {
        showsTestResultDialogOnResume(TestResult.NEGATIVE, DefaultState)
    }

    @Test
    fun showsInvalidTestResultDialogOnResumeForDefaultState() {
        showsTestResultDialogOnResume(TestResult.INVALID, DefaultState)
    }

    @Test
    fun showsPositiveTestResultDialogOnResumeForSymptomaticState() {
        showsTestResultDialogOnResume(TestResult.POSITIVE, testData.symptomaticState)
    }

    @Test
    fun showsNegativeTestResultDialogOnResumeForSymptomaticState() {
        showsTestResultDialogOnResume(TestResult.NEGATIVE, testData.symptomaticState)
    }

    @Test
    fun showsInvalidTestResultDialogOnResumeForSymptomaticState() {
        showsTestResultDialogOnResume(TestResult.INVALID, testData.symptomaticState)
    }

    @Test
    fun showsPositiveTestResultDialogOnResumeForExposedState() {
        showsTestResultDialogOnResume(TestResult.POSITIVE, testData.exposedState)
    }

    @Test
    fun showsNegativeTestResultDialogOnResumeForExposedState() {
        showsTestResultDialogOnResume(TestResult.NEGATIVE, testData.exposedState)
    }

    @Test
    fun showsInvalidTestResultDialogOnResumeForExposedState() {
        showsTestResultDialogOnResume(TestResult.INVALID, testData.exposedState)
    }

    @Test
    fun hideStatusUpdateNotificationWhenNotClicked() {
        val notificationTitle = R.string.contact_alert_notification_title

        testAppContext.simulateExposureNotificationReceived()
        testAppContext.isNotificationDisplayed(notificationTitle, isDisplayed = true)

        startStatusActivityWith(UserState.exposed(LocalDate.now()))

        testAppContext.isNotificationDisplayed(notificationTitle, isDisplayed = false)

        statusRobot.checkActivityIsDisplayed(ExposedState::class)
    }

    @Test
    fun showsEnableNotificationOnResume() {
        testAppContext.setFullValidUser(testData.defaultState)
        testAppContext.revokeNotificationsPermission()

        testAppContext.app.startTestActivity<StatusActivity>()

        statusRobot.checkEnableNotificationsIsDisplayed()
    }

    @Test
    fun doesNotEnableAllowNotificationOnResume() {
        testAppContext.setFullValidUser(testData.defaultState)
        testAppContext.grantNotificationsPermission()

        testAppContext.app.startTestActivity<StatusActivity>()

        statusRobot.checkEnableNotificationsIsNotDisplayed()
    }

    @Test
    fun grantNotificationPermission() {
        testAppContext.setFullValidUser(testData.defaultState)
        testAppContext.revokeNotificationsPermission()

        testAppContext.app.startTestActivity<StatusActivity>()

        statusRobot.clickEnableNotifications()
        testAppContext.waitUntilCannotFindText(R.string.enable_notifications_title)

        testAppContext.grantNotificationsPermission()
        testAppContext.device.pressBack()

        statusRobot.checkEnableNotificationsIsNotDisplayed()
    }
}
