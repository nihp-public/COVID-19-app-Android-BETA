package uk.nhs.nhsx.sonar.android.app.status

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.json.JSONObject
import uk.nhs.nhsx.sonar.android.app.http.jsonOf

object UserStateSerialization {

    fun serialize(state: UserState): String =
        when (state) {
            is DefaultState -> jsonOf(
                "type" to state.type(),
                "until" to state.until.millis
            )
            is EmberState -> jsonOf(
                "type" to state.type(),
                "until" to state.until.millis
            )
            is RedState -> jsonOf(
                "type" to state.type(),
                "until" to state.until.millis,
                "symptoms" to state.symptoms.map { it.toString() }
            )
            is RecoveryState -> jsonOf(
                "type" to state.type(),
                "until" to state.until.millis
            )
        }

    fun deserialize(json: String): UserState {
        val jsonObj = JSONObject(json)
        val type = jsonObj.getString("type")
        val until = DateTime(jsonObj.getLong("until"), DateTimeZone.UTC)

        return when (type) {
            "EmberState" -> EmberState(until)
            "RedState" -> RedState(until, jsonObj.getSymptoms())
            "RecoveryState" -> RecoveryState(until)
            else -> DefaultState(until)
        }
    }

    private fun UserState.type() = javaClass.simpleName

    private fun JSONObject.getSymptoms(): Set<Symptom> {
        val array = getJSONArray("symptoms")
        val symptoms = mutableSetOf<Symptom>()

        for (index in 0 until array.length()) {
            val stringValue = array.getString(index)
            val symptom = Symptom.valueOf(stringValue)
            symptoms.add(symptom)
        }

        return symptoms
    }
}
