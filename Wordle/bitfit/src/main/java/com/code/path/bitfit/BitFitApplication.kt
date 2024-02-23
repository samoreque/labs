package com.code.path.bitfit

import android.app.Application
import com.code.path.bitfit.data.db.BitFitDatabase

class BitFitApplication: Application() {
    val db by lazy { BitFitDatabase.getInstance(this) }
}