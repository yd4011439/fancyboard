package fancyboard.fonts.keyboard.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class GenericViewHolder(itemView: View) : ViewHolder(itemView) {}

@SuppressLint("ViewConstructor")
class HorizontalSelectView(
    context: Context,
    private var theme: KeyboardTheme,
    private var selectedItem: String,
    private var displayItems: Array<String>,
    private var displayFunc: (str: String) -> String = { it }
) : RecyclerView(context) {
    private inner class FontSelectAdapter : Adapter<ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val view = Button(parent.context)
            return GenericViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) {
            val button = holder.itemView as Button
            button.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, context.dpToPx(40))
            applyButtonTheme(button, theme)
            val px = context.dpToPx(8)
            val py = context.dpToPx(4)
            button.setPadding(px, py, px, py)
            button.text = displayFunc(displayItems[position])

            if(displayItems[position] == selectedItem) {
                button.backgroundTintList = ColorStateList.valueOf(theme.keyPressedColor)
            }else{
                button.backgroundTintList = ColorStateList.valueOf(theme.keyColor)
            }

            button.onTouchAction(theme, null, {
                onItemChange?.let {
                    adapter?.notifyItemChanged(displayItems.indexOf(selectedItem))
                    adapter?.notifyItemChanged(position)
                    selectedItem = displayItems[position]
                    it(displayItems[position])
                }
            })
        }

        override fun getItemCount(): Int {
            return displayItems.size
        }
    }

    private inner class HorizontalSpaceItemDecoration(private val space: Int) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: State
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position != NO_POSITION) {
                outRect.right = space
                if (position == 0) {
                    outRect.left = space // optional: space on the left of first item
                }
            }
        }
    }

    var onItemChange: ((name: String) -> Unit)? = null

    init {
        overScrollMode = OVER_SCROLL_NEVER
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, context.dpToPx(42))
        adapter = FontSelectAdapter()
        val layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        this.layoutManager = layoutManager
        val spaceInPx = context.dpToPx(4)
        addItemDecoration(HorizontalSpaceItemDecoration(spaceInPx))
        val initialTarget = displayItems.indexOf(selectedItem)
        layoutManager.scrollToPositionWithOffset(initialTarget, 20)
    }

    fun setSelectedIndex(index:Int){
        val prevIndex = displayItems.indexOf(selectedItem)
        this.selectedItem = displayItems[index]
        adapter?.notifyItemChanged(index)
        adapter?.notifyItemChanged(prevIndex)
        layoutManager?.smoothScrollToPosition(this, null, index)
    }

    fun setItem(item:String){
        val index = displayItems.indexOf(item)
        setSelectedIndex(index)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun applyTheme(theme: KeyboardTheme){
        this.theme = theme
        adapter?.notifyDataSetChanged()
    }
}