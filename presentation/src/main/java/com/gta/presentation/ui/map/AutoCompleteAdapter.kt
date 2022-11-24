package com.gta.presentation.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.gta.domain.model.LocationInfo
import com.gta.presentation.databinding.ItemMapSearchBinding
import com.gta.presentation.ui.mypage.mycars.OnItemClickListener
import java.util.*

class AutoCompleteAdapter(
    context: Context,
    list: List<LocationInfo>
) :
    ArrayAdapter<LocationInfo>(context, 0, list) {
    private val searchList: MutableList<LocationInfo> = ArrayList(list)
    private var allLocationInfo: List<LocationInfo> = ArrayList(list)
    private var listener: OnItemClickListener<LocationInfo>? = null

    override fun getCount(): Int {
        return searchList.size
    }

    override fun getItem(position: Int): LocationInfo {
        return searchList[position]
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemMapSearchBinding =
            if (convertView == null) {
                ItemMapSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            } else {
                convertView.tag as ItemMapSearchBinding
            }
        val item = getItem(position)
        binding.data = item
        binding.root.setOnClickListener {
            listener?.onClick(item)
        }
        binding.root.tag = binding
        return binding.root
    }

    fun setOnItemClickListener(listener: OnItemClickListener<LocationInfo>) {
        this.listener = listener
    }

    override fun add(newInfo: LocationInfo?) {
        newInfo ?: return
        searchList.add(newInfo)
        notifyDataSetChanged()
    }

    override fun addAll(collection: MutableCollection<out LocationInfo>) {
        searchList.addAll(collection)
        notifyDataSetChanged()
    }

    override fun insert(newInfo: LocationInfo?, index: Int) {
        newInfo ?: return
        searchList.add(index, newInfo)
        notifyDataSetChanged()
    }

    override fun remove(newDailyData: LocationInfo?) {
        searchList.remove(newDailyData)
        notifyDataSetChanged()
    }

    override fun clear() {
        searchList.clear()
        notifyDataSetChanged()
    }

    fun replace(collection: Collection<LocationInfo>) {
        searchList.clear()
        searchList.addAll(collection)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return searchFilter
    }

    private val searchFilter = object : Filter() {
        override fun convertResultToString(resultValue: Any): String {
            return (resultValue as LocationInfo).address
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            if (constraint != null) {
                val searchSuggestion: MutableList<LocationInfo> = ArrayList()
                for (search in allLocationInfo) {
                    if (search.address.lowercase()
                        .startsWith(constraint.toString().lowercase(Locale.ROOT))
                    ) {
                        searchSuggestion.add(search)
                    }
                }
                filterResults.values = searchSuggestion
                filterResults.count = searchSuggestion.size
            }
            return filterResults
        }

        override fun publishResults(
            constraint: CharSequence?,
            results: FilterResults
        ) {
            searchList.clear()
            if (results.count > 0) {
                for (result in results.values as List<*>) {
                    if (result is LocationInfo) {
                        searchList.add(result)
                    }
                }
                notifyDataSetChanged()
            } else if (constraint == null) {
                searchList.addAll(allLocationInfo)
                notifyDataSetInvalidated()
            }
        }
    }
}
