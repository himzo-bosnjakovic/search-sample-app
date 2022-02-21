package at.tug.search.models
import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Person : Parcelable{

    var personID : String? = null
    var personName : String? = null
    var title: String? = null
    var contactName : String? = null
    var tel_office : String? = null
    var email_person : String? = null
    var p_detail_infoBlock_webLink : String? = null
    var webLink_person : String? = null
    var p_street_PA : String? = null
    var p_locality_PA : String? = null
    var p_pcode_PA : String? = null
    var infoBlock_pic : String? = null
    var image : Bitmap? = null
}