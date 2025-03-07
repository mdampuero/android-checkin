package cl.clickgroup.checkin.network.requests

data class CheckInByRegistrantIDsRequest(val registrantIDs: List<Int>, val results: List<ExternalIdWithRequestValue>)

data class ExternalIdWithRequestValue(
    val externalId: Int,
    val requestValue: String?
)
