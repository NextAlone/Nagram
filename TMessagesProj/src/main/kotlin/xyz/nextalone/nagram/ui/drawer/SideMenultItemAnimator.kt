package xyz.nextalone.nagram.ui.drawer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import org.telegram.ui.Components.CubicBezierInterpolator
import org.telegram.ui.Components.RecyclerListView

class SideMenultItemAnimator(view: RecyclerListView) : SimpleItemAnimator() {

    override fun listenToAnimationUpdates(listener: Runnable) {}

    private val mPendingRemovals = ArrayList<RecyclerView.ViewHolder>()
    private val mPendingAdditions = ArrayList<RecyclerView.ViewHolder>()
    private val mPendingMoves = ArrayList<MoveInfo>()
    private val mPendingChanges = ArrayList<ChangeInfo>()

    private val mAdditionsList = ArrayList<ArrayList<RecyclerView.ViewHolder>>()
    private val mMovesList = ArrayList<ArrayList<MoveInfo>>()
    private val mChangesList = ArrayList<ArrayList<ChangeInfo>>()

    private val mAddAnimations = ArrayList<RecyclerView.ViewHolder>()
    private val mMoveAnimations = ArrayList<RecyclerView.ViewHolder>()
    private val mRemoveAnimations = ArrayList<RecyclerView.ViewHolder>()
    private val mChangeAnimations = ArrayList<RecyclerView.ViewHolder>()

    private val parentRecyclerView: RecyclerListView = view

    init {
        view.setChildDrawingOrderCallback { childCount, i ->
            when {
                i == childCount - 1 -> 0
                i >= 0 -> i + 1
                else -> i
            }
        }
    }

    private class MoveInfo(
        val holder: RecyclerView.ViewHolder,
        val fromX: Int, val fromY: Int,
        val toX: Int, val toY: Int,
    )

    private class ChangeInfo(
        var oldHolder: RecyclerView.ViewHolder?,
        var newHolder: RecyclerView.ViewHolder?,
        val fromX: Int = 0, val fromY: Int = 0,
        val toX: Int = 0, val toY: Int = 0,
    ) {
        override fun toString() =
            "ChangeInfo{oldHolder=$oldHolder, newHolder=$newHolder, fromX=$fromX, fromY=$fromY, toX=$toX, toY=$toY}"
    }

    override fun runPendingAnimations() {
        val removalsPending = mPendingRemovals.isNotEmpty()
        val movesPending = mPendingMoves.isNotEmpty()
        val changesPending = mPendingChanges.isNotEmpty()
        val additionsPending = mPendingAdditions.isNotEmpty()
        if (!removalsPending && !movesPending && !additionsPending && !changesPending) return

        var animatingHeight = 0
        for (holder in mPendingRemovals) animatingHeight += holder.itemView.measuredHeight
        for (holder in mPendingRemovals) animateRemoveImpl(holder, animatingHeight)
        mPendingRemovals.clear()

        if (movesPending) {
            val moves = ArrayList(mPendingMoves)
            mMovesList.add(moves)
            mPendingMoves.clear()
            for (moveInfo in moves) {
                animateMoveImpl(moveInfo.holder, moveInfo.fromX, moveInfo.fromY, moveInfo.toX, moveInfo.toY)
            }
            moves.clear()
            mMovesList.remove(moves)
        }
        if (changesPending) {
            val changes = ArrayList(mPendingChanges)
            mChangesList.add(changes)
            mPendingChanges.clear()
            for (change in changes) animateChangeImpl(change)
            changes.clear()
            mChangesList.remove(changes)
        }
        if (additionsPending) {
            val additions = ArrayList(mPendingAdditions)
            mAdditionsList.add(additions)
            mPendingAdditions.clear()
            animatingHeight = 0
            for (holder in additions) animatingHeight += holder.itemView.measuredHeight
            for ((i, holder) in additions.withIndex()) {
                animateAddImpl(holder, i, additions.size, animatingHeight)
            }
            additions.clear()
            mAdditionsList.remove(additions)
        }
        parentRecyclerView.invalidateViews()
        parentRecyclerView.invalidate()
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder, info: ItemHolderInfo): Boolean {
        resetAnimation(holder)
        mPendingRemovals.add(holder)
        return true
    }

    private fun animateRemoveImpl(holder: RecyclerView.ViewHolder, totalHeight: Int) {
        val view = holder.itemView
        val animation = view.animate()
        mRemoveAnimations.add(holder)
        animation.setDuration(220).translationY(-totalHeight.toFloat())
            .setInterpolator(CubicBezierInterpolator.EASE_OUT)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animator: Animator) = dispatchRemoveStarting(holder)
                override fun onAnimationEnd(animator: Animator) {
                    animation.setListener(null)
                    dispatchRemoveFinished(holder)
                    mRemoveAnimations.remove(holder)
                    dispatchFinishedWhenDone()
                }
            }).start()
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        resetAnimation(holder)
        mPendingAdditions.add(holder)
        holder.itemView.alpha = 0f
        return true
    }

    private fun animateAddImpl(holder: RecyclerView.ViewHolder, num: Int, addCount: Int, totalHeight: Int) {
        val view = holder.itemView
        val animation = view.animate()
        mAddAnimations.add(holder)
        view.alpha = 1f
        view.translationY = -totalHeight.toFloat()
        animation.translationY(0f).setDuration(220).setInterpolator(CubicBezierInterpolator.EASE_OUT)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animator: Animator) = dispatchAddStarting(holder)
                override fun onAnimationCancel(animator: Animator) { view.translationY = 0f }
                override fun onAnimationEnd(animator: Animator) {
                    animation.setListener(null)
                    dispatchAddFinished(holder)
                    mAddAnimations.remove(holder)
                    dispatchFinishedWhenDone()
                }
            }).start()
    }

    override fun animateMove(
        holder: RecyclerView.ViewHolder, info: ItemHolderInfo?,
        fromX: Int, fromY: Int, toX: Int, toY: Int,
    ): Boolean {
        val view = holder.itemView
        val fx = fromX + holder.itemView.translationX.toInt()
        val fy = fromY + holder.itemView.translationY.toInt()
        resetAnimation(holder)
        val deltaX = toX - fx
        val deltaY = toY - fy
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder)
            return false
        }
        if (deltaX != 0) view.translationX = (-deltaX).toFloat()
        if (deltaY != 0) view.translationY = (-deltaY).toFloat()
        mPendingMoves.add(MoveInfo(holder, fx, fy, toX, toY))
        return true
    }

    private fun animateMoveImpl(holder: RecyclerView.ViewHolder, fromX: Int, fromY: Int, toX: Int, toY: Int) {
        val view = holder.itemView
        val deltaX = toX - fromX
        val deltaY = toY - fromY
        if (deltaX != 0) view.animate().translationX(0f)
        if (deltaY != 0) view.animate().translationY(0f)
        val animation = view.animate()
        mMoveAnimations.add(holder)
        animation.setDuration(220).setInterpolator(CubicBezierInterpolator.EASE_OUT)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animator: Animator) = dispatchMoveStarting(holder)
                override fun onAnimationCancel(animator: Animator) {
                    if (deltaX != 0) view.translationX = 0f
                    if (deltaY != 0) view.translationY = 0f
                }
                override fun onAnimationEnd(animator: Animator) {
                    animation.setListener(null)
                    dispatchMoveFinished(holder)
                    mMoveAnimations.remove(holder)
                    dispatchFinishedWhenDone()
                }
            }).start()
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder, newHolder: RecyclerView.ViewHolder?,
        info: ItemHolderInfo?, fromX: Int, fromY: Int, toX: Int, toY: Int,
    ): Boolean {
        if (oldHolder === newHolder) {
            return animateMove(oldHolder, null, fromX, fromY, toX, toY)
        }
        val prevTranslationX = oldHolder.itemView.translationX
        val prevTranslationY = oldHolder.itemView.translationY
        val prevAlpha = oldHolder.itemView.alpha
        resetAnimation(oldHolder)
        val deltaX = (toX - fromX - prevTranslationX).toInt()
        val deltaY = (toY - fromY - prevTranslationY).toInt()
        oldHolder.itemView.translationX = prevTranslationX
        oldHolder.itemView.translationY = prevTranslationY
        oldHolder.itemView.alpha = prevAlpha
        if (newHolder != null) {
            resetAnimation(newHolder)
            newHolder.itemView.translationX = (-deltaX).toFloat()
            newHolder.itemView.translationY = (-deltaY).toFloat()
            newHolder.itemView.alpha = 0f
        }
        mPendingChanges.add(ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY))
        return true
    }

    private fun animateChangeImpl(changeInfo: ChangeInfo) {
        val holder = changeInfo.oldHolder
        val view = holder?.itemView
        val newHolder = changeInfo.newHolder
        val newView = newHolder?.itemView
        if (view != null) {
            val oldViewAnim = view.animate().setDuration(changeRemoveDuration)
            mChangeAnimations.add(changeInfo.oldHolder!!)
            oldViewAnim.translationX((changeInfo.toX - changeInfo.fromX).toFloat())
            oldViewAnim.translationY((changeInfo.toY - changeInfo.fromY).toFloat())
            oldViewAnim.alpha(0f).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animator: Animator) =
                    dispatchChangeStarting(changeInfo.oldHolder, true)
                override fun onAnimationEnd(animator: Animator) {
                    oldViewAnim.setListener(null)
                    view.alpha = 1f
                    view.translationX = 0f
                    view.translationY = 0f
                    dispatchChangeFinished(changeInfo.oldHolder, true)
                    changeInfo.oldHolder?.let { mChangeAnimations.remove(it) }
                    dispatchFinishedWhenDone()
                }
            }).start()
        }
        if (newView != null) {
            val newViewAnimation = newView.animate()
            mChangeAnimations.add(changeInfo.newHolder!!)
            newViewAnimation.translationX(0f).translationY(0f)
                .setDuration(changeAddDuration)
                .setStartDelay(changeDuration - changeAddDuration)
                .alpha(1f).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animator: Animator) =
                        dispatchChangeStarting(changeInfo.newHolder, false)
                    override fun onAnimationEnd(animator: Animator) {
                        newViewAnimation.setListener(null)
                        newView.alpha = 1f
                        newView.translationX = 0f
                        newView.translationY = 0f
                        dispatchChangeFinished(changeInfo.newHolder, false)
                        changeInfo.newHolder?.let { mChangeAnimations.remove(it) }
                        dispatchFinishedWhenDone()
                    }
                }).start()
        }
    }

    private fun endChangeAnimation(infoList: MutableList<ChangeInfo>, item: RecyclerView.ViewHolder) {
        for (i in infoList.indices.reversed()) {
            val changeInfo = infoList[i]
            if (endChangeAnimationIfNecessary(changeInfo, item)) {
                if (changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                    infoList.remove(changeInfo)
                }
            }
        }
    }

    private fun endChangeAnimationIfNecessary(changeInfo: ChangeInfo) {
        if (changeInfo.oldHolder != null) endChangeAnimationIfNecessary(changeInfo, changeInfo.oldHolder!!)
        if (changeInfo.newHolder != null) endChangeAnimationIfNecessary(changeInfo, changeInfo.newHolder!!)
    }

    private fun endChangeAnimationIfNecessary(changeInfo: ChangeInfo, item: RecyclerView.ViewHolder): Boolean {
        val oldItem: Boolean
        when (item) {
            changeInfo.newHolder -> { changeInfo.newHolder = null; oldItem = false }
            changeInfo.oldHolder -> { changeInfo.oldHolder = null; oldItem = true }
            else -> return false
        }
        item.itemView.alpha = 1f
        item.itemView.translationX = 0f
        item.itemView.translationY = 0f
        dispatchChangeFinished(item, oldItem)
        return true
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        val view = item.itemView
        view.animate().cancel()
        for (i in mPendingMoves.indices.reversed()) {
            val moveInfo = mPendingMoves[i]
            if (moveInfo.holder === item) {
                view.translationY = 0f
                view.translationX = 0f
                dispatchMoveFinished(item)
                mPendingMoves.removeAt(i)
            }
        }
        endChangeAnimation(mPendingChanges, item)
        if (mPendingRemovals.remove(item)) {
            view.translationY = 0f
            dispatchRemoveFinished(item)
        }
        if (mPendingAdditions.remove(item)) {
            view.translationY = 0f
            dispatchAddFinished(item)
        }
        for (i in mChangesList.indices.reversed()) {
            val changes = mChangesList[i]
            endChangeAnimation(changes, item)
            if (changes.isEmpty()) mChangesList.removeAt(i)
        }
        for (i in mMovesList.indices.reversed()) {
            val moves = mMovesList[i]
            for (j in moves.indices.reversed()) {
                val moveInfo = moves[j]
                if (moveInfo.holder === item) {
                    view.translationY = 0f
                    view.translationX = 0f
                    dispatchMoveFinished(item)
                    moves.removeAt(j)
                    if (moves.isEmpty()) mMovesList.removeAt(i)
                    break
                }
            }
        }
        for (i in mAdditionsList.indices.reversed()) {
            val additions = mAdditionsList[i]
            if (additions.remove(item)) {
                view.translationY = 0f
                dispatchAddFinished(item)
                if (additions.isEmpty()) mAdditionsList.removeAt(i)
            }
        }
        dispatchFinishedWhenDone()
    }

    private fun resetAnimation(holder: RecyclerView.ViewHolder) {
        if (sDefaultInterpolator == null) {
            sDefaultInterpolator = ValueAnimator().interpolator
        }
        holder.itemView.animate().setInterpolator(sDefaultInterpolator)
        endAnimation(holder)
    }

    override fun isRunning(): Boolean =
        mPendingAdditions.isNotEmpty() ||
        mPendingChanges.isNotEmpty() ||
        mPendingMoves.isNotEmpty() ||
        mPendingRemovals.isNotEmpty() ||
        mMoveAnimations.isNotEmpty() ||
        mRemoveAnimations.isNotEmpty() ||
        mAddAnimations.isNotEmpty() ||
        mChangeAnimations.isNotEmpty() ||
        mMovesList.isNotEmpty() ||
        mAdditionsList.isNotEmpty() ||
        mChangesList.isNotEmpty()

    private fun dispatchFinishedWhenDone() {
        if (!isRunning()) dispatchAnimationsFinished()
    }

    override fun endAnimations() {
        var count = mPendingMoves.size
        for (i in count - 1 downTo 0) {
            val item = mPendingMoves[i]
            item.holder.itemView.translationY = 0f
            item.holder.itemView.translationX = 0f
            dispatchMoveFinished(item.holder)
            mPendingMoves.removeAt(i)
        }
        count = mPendingRemovals.size
        for (i in count - 1 downTo 0) {
            val item = mPendingRemovals[i]
            dispatchRemoveFinished(item)
            mPendingRemovals.removeAt(i)
        }
        count = mPendingAdditions.size
        for (i in count - 1 downTo 0) {
            val item = mPendingAdditions[i]
            item.itemView.translationY = 0f
            dispatchAddFinished(item)
            mPendingAdditions.removeAt(i)
        }
        count = mPendingChanges.size
        for (i in count - 1 downTo 0) endChangeAnimationIfNecessary(mPendingChanges[i])
        mPendingChanges.clear()
        if (!isRunning()) return

        var listCount = mMovesList.size
        for (i in listCount - 1 downTo 0) {
            val moves = mMovesList[i]
            count = moves.size
            for (j in count - 1 downTo 0) {
                val moveInfo = moves[j]
                val item = moveInfo.holder
                item.itemView.translationY = 0f
                item.itemView.translationX = 0f
                dispatchMoveFinished(moveInfo.holder)
                moves.removeAt(j)
                if (moves.isEmpty()) mMovesList.remove(moves)
            }
        }
        listCount = mAdditionsList.size
        for (i in listCount - 1 downTo 0) {
            val additions = mAdditionsList[i]
            count = additions.size
            for (j in count - 1 downTo 0) {
                val item = additions[j]
                item.itemView.translationY = 0f
                dispatchAddFinished(item)
                additions.removeAt(j)
                if (additions.isEmpty()) mAdditionsList.remove(additions)
            }
        }
        listCount = mChangesList.size
        for (i in listCount - 1 downTo 0) {
            val changes = mChangesList[i]
            count = changes.size
            for (j in count - 1 downTo 0) {
                endChangeAnimationIfNecessary(changes[j])
                if (changes.isEmpty()) mChangesList.remove(changes)
            }
        }

        cancelAll(mRemoveAnimations)
        cancelAll(mMoveAnimations)
        cancelAll(mAddAnimations)
        cancelAll(mChangeAnimations)

        dispatchAnimationsFinished()
    }

    private fun cancelAll(viewHolders: List<RecyclerView.ViewHolder>) {
        for (i in viewHolders.indices.reversed()) {
            viewHolders[i].itemView.animate().cancel()
        }
    }

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder, payloads: List<Any>): Boolean =
        payloads.isNotEmpty() || super.canReuseUpdatedViewHolder(viewHolder, payloads)

    companion object {
        private var sDefaultInterpolator: TimeInterpolator? = null
    }
}
