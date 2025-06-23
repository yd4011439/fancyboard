package fancyboard.fonts.keyboard.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import fancyboard.fonts.keyboard.data.allSymbolCategories
import fancyboard.fonts.keyboard.data.allSymbols

@SuppressLint("ViewConstructor")
class SymbolView(context: Context, theme: KeyboardTheme, selectedCategory: String) :
    BaseKeyView(context, theme) {
    private val pager: ViewPager2
    private val faceSelect = HorizontalSelectView(
        context, theme, selectedCategory,
        allSymbolCategories
    )
    var onCategoryChange: ((name: String) -> Unit)? = null

    private inner class PaddingItemDecoration(
        private val left: Int,
        private val top: Int,
        private val right: Int,
        private val bottom: Int
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.set(left, top, right, bottom)
        }
    }


    private inner class RecyclerViewAdapter(
        private val items: Array<String>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount() = items.size

        override fun getItemViewType(position: Int): Int {
            return 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val tv = Button(parent.context).apply {
                textSize = 16f
                layoutParams = LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            return GenericViewHolder(tv)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = items[position]
            val button = holder.itemView as Button
            applyButtonTheme(button, theme)
            button.text = item
            button.onTouchAction(theme, null,  {
                currentInputConnection?.commitText(item, 1)
            })
        }
    }

    private inner class TextFacePagerAdapter :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemCount() = allSymbolCategories.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val recycler = RecyclerView(parent.context).apply {
                layoutManager = GridLayoutManager(context, 8)
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
                )
                addItemDecoration(PaddingItemDecoration(4, 4, 4, 4))
            }
            return object : RecyclerView.ViewHolder(recycler) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val category = allSymbolCategories[position]
            (holder.itemView as RecyclerView).adapter = RecyclerViewAdapter(allSymbols[category]!!)
        }
    }

    init {
        val container: LinearLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

            pager = ViewPager2(context).apply {
                layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f)
            }
            pager.adapter = TextFacePagerAdapter()
            pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    faceSelect.setSelectedIndex(position)
                    onCategoryChange?.invoke(allSymbolCategories[position])
                }
            })

            faceSelect.onItemChange = {
                pager.currentItem = allSymbolCategories.indexOf(it)
                onCategoryChange?.invoke(it)
            }

            pager.clipToPadding = false
            pager.setPadding(0, context.dpToPx(4), 0, context.dpToPx(4))
            addView(faceSelect)
            addView(pager)
        }
        addView(container)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun applyTheme(theme: KeyboardTheme) {
        super.applyTheme(theme)
        faceSelect.applyTheme(theme)
        pager.adapter?.notifyDataSetChanged()
    }
}