package fancyboard.fonts.keyboard.keyboard


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.TextPaint
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import fancyboard.fonts.keyboard.data.allEmoji


@SuppressLint("ViewConstructor")
class EmojiView(
    context: Context,
    theme: KeyboardTheme
) : BaseKeyView(context, theme) {
    private val categories: List<Pair<String, List<String>>> = getDrawableEmoji()

    private val container: LinearLayout
    private val viewPager: ViewPager2
    private val catSelect = HorizontalSelectView(context, theme, emojiCategories[0], emojiCategories)

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

            addView(catSelect)

            // ViewPager2
            viewPager = ViewPager2(context).apply {
                layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f)
                adapter = EmojiPagerAdapter()
            }
            addView(viewPager)
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    catSelect.setSelectedIndex(position)
                }
            })

            catSelect.onItemChange = {
                viewPager.currentItem = emojiCategories.indexOf(it)
            }

        }

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        setBackgroundColor(Color.TRANSPARENT)
        addView(container)
    }

    private inner class EmojiPagerAdapter :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemCount() = categories.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val recycler = RecyclerView(parent.context).apply {
                layoutManager = GridLayoutManager(context, 8)
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
                )
            }
            return object : RecyclerView.ViewHolder(recycler) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val emojis = categories[position].second
            (holder.itemView as RecyclerView).adapter = EmojiGridAdapter(emojis)
        }
    }

    private inner class EmojiGridAdapter(
        private val items: List<String>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount() = items.size

        override fun getItemViewType(position: Int): Int {
            return 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val tv = TextView(parent.context).apply {
                textSize = 22f
                setTextColor(Color.BLACK)
                alpha = 1f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(context.dpToPx(48), context.dpToPx(48), 1f)
            }

            return EmojiViewHolder(tv)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = items[position]
            (holder as EmojiViewHolder).bind(item)
        }
    }


    private inner class EmojiViewHolder(view: TextView) : RecyclerView.ViewHolder(view) {
        fun bind(emoji: String) {
            (itemView as TextView).apply {
                text = emoji
                onTouchAction(theme, {}) {
                    currentInputConnection?.commitText(text, 1)
                }
            }
        }
    }

    override fun applyTheme(theme: KeyboardTheme) {
        super.applyTheme(theme)
    }

    companion object {
        private var drawableEmoji: List<Pair<String, List<String>>>? = null
        private var emojiCategories: Array<String> = allEmoji.map { it.first }.toTypedArray()
        private fun getDrawableEmoji(): List<Pair<String, List<String>>> {
            if (drawableEmoji == null) {
                synchronized(this) {
                    val paint = TextPaint()
                    drawableEmoji =
                        allEmoji.map { cat ->
                            cat.copy(second = cat.second.filter {
                                paint.hasGlyph(it)
                            })
                        }
                }
            }

            return drawableEmoji!!
        }
    }
}
