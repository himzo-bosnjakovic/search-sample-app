package at.tug.search.utils

import android.util.Log
import android.view.View
import at.tug.search.models.*
import org.w3c.dom.Element
import org.w3c.dom.Text
import org.xml.sax.InputSource
import java.io.StringReader
import java.lang.Exception
import javax.xml.parsers.DocumentBuilderFactory


class XMLParser {

    companion object{
        val instance = XMLParser()
    }

    private var TAG = "XMLParser"

    fun courseModelsFromData(xmlString: String, lamda : (ArrayList<Course>)->Unit) {

        try {
            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val xmlInput = InputSource(StringReader(xmlString))
            val doc = dBuilder.parse(xmlInput)
            val nList = doc.getElementsByTagName("MODULE_RESULT")

            val coursesList: ArrayList<Course> = ArrayList()

            for (i in 0 until nList.length) {

                val fieldList = (nList.item(i) as Element).getElementsByTagName("Field")
                val course = Course()

                for (j in 0 until fieldList.length)
                {
                    val elementName: String = (fieldList.item(j) as Element).getAttribute("name")
                    val elementData: String = (fieldList.item(j).childNodes.item(0) as Text).data

                    when (elementName){
                        "name" -> course.name = elementData
                        "courseName" -> course.courseName = elementData
                        "teachingTerm" -> course.teachingTerm = elementData
                        "hoursPerWeek" -> course.hoursPerWeek = elementData
                        "teachingActivityID" -> course.teachingActivityID = elementData
                        "courseCAMPUSonlineURL" -> course.courseCAMPUSonlineURL = elementData
                        "lvDetailCAMPUSonlineURL" -> course.lvDetailCAMPUSonlineURL = elementData
                        "id_c" -> course.id_c = elementData
                    }
                }
                coursesList.add(course)
            }
            lamda(coursesList)
        }
        catch (exception : Exception)
        {
            Log.e("XMLParser", "error in courseModelsFromData: " + exception.toString())
        }
    }

    fun eventModelsFromData(xmlString: String, lamda : (ArrayList<Event>)->Unit) {

        try
        {
            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val xmlInput = InputSource(StringReader(xmlString))
            val doc = dBuilder.parse(xmlInput)
            val nList = doc.getElementsByTagName("item")

            val eventList: ArrayList<Event> = ArrayList()

            for (i in 0 until nList.length) {

                val event = Event()

                event.title = (nList.item(i) as Element).getElementsByTagName("title")?.item(0)?.firstChild?.nodeValue
                event.link = (nList.item(i) as Element).getElementsByTagName("link")?.item(0)?.firstChild?.nodeValue
                event.guid = (nList.item(i) as Element).getElementsByTagName("guid")?.item(0)?.firstChild?.nodeValue
                event.descriptionText = (nList.item(i) as Element).getElementsByTagName("description")?.item(0)?.firstChild?.nodeValue
                event.pubDate = (nList.item(i) as Element).getElementsByTagName("pubDate")?.item(0)?.firstChild?.nodeValue
                event.author = (nList.item(i) as Element).getElementsByTagName("author")?.item(0)?.firstChild?.nodeValue
                event.category = (nList.item(i) as Element).getElementsByTagName("category")?.item(0)?.firstChild?.nodeValue

                eventList.add(event)
            }
            lamda(eventList)
        }
        catch (exception : Exception)
        {
            Log.e("XMLParser", "error in eventModelsFromData: " + exception.toString())
        }
    }

    fun newsModelsFromData(xmlString: String, lamda : (ArrayList<News>)->Unit) {

        try{
            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val xmlInput = InputSource(StringReader(xmlString))
            val doc = dBuilder.parse(xmlInput)
            val nList = doc.getElementsByTagName("item")

            val newsList: ArrayList<News> = ArrayList()

            for (i in 0 until nList.length) {

                val news = News()

                news.title = (nList.item(i) as Element).getElementsByTagName("title")?.item(0)?.firstChild?.nodeValue
                news.link = (nList.item(i) as Element).getElementsByTagName("link")?.item(0)?.firstChild?.nodeValue
                news.guid = (nList.item(i) as Element).getElementsByTagName("guid")?.item(0)?.firstChild?.nodeValue
                news.descriptionText = (nList.item(i) as Element).getElementsByTagName("description")?.item(0)?.firstChild?.nodeValue
                news.pubDate = (nList.item(i) as Element).getElementsByTagName("pubDate")?.item(0)?.firstChild?.nodeValue
                news.author = (nList.item(i) as Element).getElementsByTagName("author")?.item(0)?.firstChild?.nodeValue
                news.category = (nList.item(i) as Element).getElementsByTagName("category")?.item(0)?.firstChild?.nodeValue

                newsList.add(news)
            }
            lamda(newsList)
        }
        catch (exception : Exception)
        {
            Log.e("XMLParser", "error in newsModelsFromData: " + exception.toString())
        }
    }

    fun organisationModelsFromData(xmlString: String, lamda : (ArrayList<Organisation>)->Unit) {

        try
        {
            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val xmlInput = InputSource(StringReader(xmlString))
            val doc = dBuilder.parse(xmlInput)
            val nList = doc.getElementsByTagName("MODULE_RESULT")

            val organisationList: ArrayList<Organisation> = ArrayList()

            for (i in 0 until nList.length) {

                val fieldList = (nList.item(i) as Element).getElementsByTagName("Field")
                val organisation = Organisation()

                for (j in 0 until fieldList.length)
                {
                    val elementName: String = (fieldList.item(j) as Element).getAttribute("name")
                    val elementData: String = (fieldList.item(j).childNodes.item(0) as Text).data

                    when (elementName){
                        "name" -> organisation.name = elementData
                        "orgUnitID" -> organisation.orgUnitID = elementData
                        "userDefname" -> organisation.userDefname = elementData
                        "street" -> organisation.street = elementData
                        "locality" -> organisation.locality = elementData
                        "pcode" -> organisation.pcode = elementData
                        "email" -> organisation.email = elementData
                        "href" -> organisation.href = elementData
                        "locationURL" -> organisation.locationURL = elementData
                        "CAMPUSonlineURL" -> organisation.CAMPUSonlineURL = elementData
                    }
                }
                organisationList.add(organisation)
            }
            lamda(organisationList)
        }
        catch (exception : Exception)
        {
            Log.e("XMLParser", "error in organisationModelsFromData: " + exception.toString())
        }
    }


    fun personModelsFromData(xmlString: String, lamda : (ArrayList<Person>)->Unit){

        try
        {
            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val xmlInput = InputSource(StringReader(xmlString))
            val doc = dBuilder.parse(xmlInput)
            val nList = doc.getElementsByTagName("MODULE_RESULT")

            val personsList: ArrayList<Person> = ArrayList()

            for (i in 0 until nList.length) {

                val fieldList = (nList.item(i) as Element).getElementsByTagName("Field")
                val person = Person()

                for (j in 0 until fieldList.length)
                {
                    val elementName: String = (fieldList.item(j) as Element).getAttribute("name")
                    val elementData: String = (fieldList.item(j).childNodes.item(0) as Text).data

                    when (elementName){
                        "personID" -> person.personID = elementData
                        "personName" -> person.personName = elementData
                        "title" -> person.title = elementData
                        "contactName" -> person.contactName = elementData
                        "tel_office" -> person.tel_office = elementData
                        "email_person" -> person.email_person = elementData
                        "p_detail_infoBlock_webLink" -> person.p_detail_infoBlock_webLink = elementData
                        "webLink_person" -> person.webLink_person = elementData
                        "p_street_PA" -> person.p_street_PA = elementData
                        "p_locality_PA" -> person.p_locality_PA = elementData
                        "p_pcode_PA" -> person.p_pcode_PA = elementData
                        "infoBlock_pic" -> person.infoBlock_pic = elementData
                    }
                }
                personsList.add(person)
            }
            lamda(personsList)
        }
        catch (exception : Exception)
        {
            Log.e("XMLParser", "error in personModelsFromData: " + exception.toString())
        }
    }

    fun roomModelsFromData(xmlString: String, lamda : (ArrayList<Room>)->Unit){

        try
        {
            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val xmlInput = InputSource(StringReader(xmlString))
            val doc = dBuilder.parse(xmlInput)
            val nList = doc.getElementsByTagName("MODULE_RESULT")

            val roomList: ArrayList<Room> = ArrayList()

            for (i in 0 until nList.length) {

                val fieldList = (nList.item(i) as Element).getElementsByTagName("Field")
                val room = Room()

                for (j in 0 until fieldList.length)
                {
                    val elementName: String = (fieldList.item(j) as Element).getAttribute("name")
                    val elementData: String = (fieldList.item(j).childNodes.item(0) as Text).data

                    when (elementName){
                        "roomID_text" -> room.roomID_text = elementData
                        "address" -> room.address = elementData
                        "purpose" -> room.purpose = elementData
                        "roomCode" -> room.roomCode = elementData
                        "additionalInformation" -> room.additionalInformation = elementData
                        "address_attrAltUrl" -> room.address_attrAltUrl = elementData
                    }
                }
                roomList.add(room)
            }
            lamda(roomList)
        }
        catch (exception : Exception)
        {
            Log.e("XMLParser", "error in roomModelsFromData: " + exception.toString())
        }
    }


}