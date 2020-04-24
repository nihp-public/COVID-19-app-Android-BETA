/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package uk.nhs.nhsx.sonar.android.app.contactevents

import android.util.Base64
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Seconds
import uk.nhs.nhsx.sonar.android.app.diagnose.review.CoLocationEvent
import uk.nhs.nhsx.sonar.android.app.util.toUtcIsoFormat
import javax.inject.Inject

class CoLocationDataProvider @Inject constructor(
    private val contactEventDao: ContactEventDao
) {

    suspend fun getEvents(): List<CoLocationEvent> {
        return contactEventDao.getAll().map(::convert)
    }

    private fun convert(contactEvent: ContactEvent): CoLocationEvent {
        val startTime = DateTime(contactEvent.timestamp, DateTimeZone.UTC)
        return CoLocationEvent(
            encryptedRemoteContactId = Base64.encodeToString(
                contactEvent.sonarId,
                Base64.DEFAULT
            ),
            rssiValues = contactEvent.rssiValues,
            rssiOffsets = contactEvent.rssiTimestamps.map {
                Seconds.secondsBetween(startTime, DateTime(it, DateTimeZone.UTC)).seconds
            },
            timestamp = startTime.toUtcIsoFormat(),
            duration = contactEvent.duration
        )
    }

    suspend fun clearData() {
        contactEventDao.clearEvents()
    }
}
