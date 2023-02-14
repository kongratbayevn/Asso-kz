package kz.app.asso.system.dto

import java.sql.Timestamp

class EventLogs {
    var title: String? = null
    var description: String? = null
    var subjectId: String? = null
    var objectId: String? = null
    var date: Timestamp = Timestamp(System.currentTimeMillis())
        private set
}
