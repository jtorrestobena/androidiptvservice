package com.bytecoders.androidtv.iptvservice.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class ApplicationItem(@StringRes val title: Int, @StringRes val message: Int, @DrawableRes val drawableRes: Int, val clickAction: () -> Unit)