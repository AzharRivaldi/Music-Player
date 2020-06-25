package com.azhar.musicplaylist.model

import java.io.Serializable

/**
 * Created by Azhar Rivaldi on 22-12-2019.
 */

class ModelListLagu : Serializable {

    var strId: String? = null

    @JvmField
    var strJudulMusic: String? = null

    @JvmField
    var strNamaBand: String? = null

    @JvmField
    var strCoverLagu: String? = null

}