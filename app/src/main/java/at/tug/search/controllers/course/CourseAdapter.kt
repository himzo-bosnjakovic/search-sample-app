package at.tug.search.controllers.course

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import at.tug.search.R
import at.tug.search.models.Course
import at.tug.search.utils.ObjectCache

class CourseAdapter (var con: Context, var resources: Int, var items : List<Course>): ArrayAdapter<Course>(con, resources, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(con)
        val view : View = layoutInflater.inflate(resources, null)

        val courseName : TextView = view.findViewById(R.id.courseName)
        val courseHours : TextView = view.findViewById(R.id.courseHours)
        val courseInstitute : TextView = view.findViewById(R.id.courseInstitute)

        val item : Course = items[position]
        if (position == 0) {
            ObjectCache.searchedCourses.clear()
        }
        ObjectCache.searchedCourses.add(item)

        courseName.text = item.courseName
        courseHours.text = item.hoursPerWeek + ", " + item.teachingTerm
        courseInstitute.text = item.name

        return view
    }
}
