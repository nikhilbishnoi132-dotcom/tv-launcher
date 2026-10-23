package com.example.tvlauncher

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class AppEntry(val label: String, val image: Drawable, val intent: Intent)

class MainActivity : AppCompatActivity() {
    private lateinit var title: TextView
    private lateinit var list: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = findViewById(R.id.title)
        list = findViewById(R.id.list)
        list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        onBackPressedDispatcher.addCallback(this) { /* a launcher swallows Back */ }
    }

    override fun onResume() {
        super.onResume()
        list.adapter = Adapter(loadApps())
        list.post { list.getChildAt(0)?.requestFocus() }
    }

    private fun loadApps(): List<AppEntry> {
        val pm = packageManager
        val seen = HashSet<String>()
        val out = mutableListOf<AppEntry>()
        for (cat in listOf(Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER)) {
            val query = Intent(Intent.ACTION_MAIN).addCategory(cat)
            for (ri in pm.queryIntentActivities(query, 0)) {
                val pkg = ri.activityInfo.packageName
                if (pkg == packageName || !seen.add(pkg)) continue
                val launch = pm.getLeanbackLaunchIntentForPackage(pkg)
                    ?: pm.getLaunchIntentForPackage(pkg) ?: continue
                val img = ri.activityInfo.loadBanner(pm) ?: ri.loadIcon(pm)
                out += AppEntry(ri.loadLabel(pm).toString(), img, launch)
            }
        }
        return out.sortedBy { it.label.lowercase() }
    }

    inner class Adapter(private val apps: List<AppEntry>) : RecyclerView.Adapter<Adapter.VH>() {
        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val icon: ImageView = v.findViewById(R.id.icon)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false))

        override fun getItemCount() = apps.size

        override fun onBindViewHolder(h: VH, position: Int) {
            val app = apps[position]
            h.icon.setImageDrawable(app.image)
            h.itemView.setOnClickListener {
                startActivity(Intent(app.intent).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            h.itemView.setOnFocusChangeListener { v, focused ->
                val s = if (focused) 1.18f else 1.0f
                v.animate().scaleX(s).scaleY(s).setDuration(160).start()
                v.elevation = if (focused) 28f else 0f
                v.alpha = if (focused) 1f else 0.82f
                if (focused) title.text = app.label
            }
        }
    }
}
