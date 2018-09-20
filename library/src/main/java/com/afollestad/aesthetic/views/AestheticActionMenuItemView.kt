/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.view.menu.ActionMenuItemView
import com.afollestad.aesthetic.ActiveInactiveColors
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onMainThread
import com.afollestad.aesthetic.utils.one
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.tint
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable

/** @author Aidan Follestad (afollestad) */
@SuppressLint("RestrictedApi")
internal class AestheticActionMenuItemView(
  context: Context,
  attrs: AttributeSet? = null
) : ActionMenuItemView(context, attrs) {

  private var icon: Drawable? = null
  private var subscription: Disposable? = null

  private fun invalidateColors(@NonNull colors: ActiveInactiveColors) {
    if (icon != null) {
      setIcon(icon!!, colors.toEnabledSl())
    }
    setTextColor(colors.activeColor)
  }

  override fun setIcon(icon: Drawable) {
    super.setIcon(icon)

    // We need to retrieve the color again here.
    // For some reason, without this, a transparent color is used and the icon disappears
    // when the overflow menu opens.
    Aesthetic.get()
        .colorIconTitle(null)
        .onMainThread()
        .one()
        .subscribeTo(::invalidateColors)
  }

  @Suppress("MemberVisibilityCanBePrivate")
  fun setIcon(
    icon: Drawable,
    colors: ColorStateList
  ) {
    this.icon = icon
    super.setIcon(icon.tint(colors))
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    subscription = Aesthetic.get()
        .colorIconTitle(null)
        .distinctToMainThread()
        .subscribeTo(::invalidateColors)
  }

  override fun onDetachedFromWindow() {
    subscription?.dispose()
    super.onDetachedFromWindow()
  }
}