package at.tug.search.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Room : Parcelable{

    var roomID_text : String? = null
    var address : String? = null
    var purpose : String? = null
    var roomCode : String? = null
    var additionalInformation : String? = null
    var address_attrAltUrl : String? = null
}