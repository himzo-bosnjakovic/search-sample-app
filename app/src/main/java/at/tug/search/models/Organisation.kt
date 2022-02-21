package at.tug.search.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Organisation : Parcelable{

    var orgUnitID : String? = null
    var name : String? = null
    var userDefname : String? = null
    var street : String? = null
    var locality : String? = null
    var pcode : String? = null
    var email : String? = null
    var href : String? = null
    var locationURL : String? = null
    var CAMPUSonlineURL : String? = null
}