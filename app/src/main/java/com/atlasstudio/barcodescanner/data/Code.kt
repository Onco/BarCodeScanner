package com.atlasstudio.barcodescanner.data

enum class CodeType {
    None,
    EAN13
}

data class Code (
    var value : String?,
    var type : CodeType
    )
{
    init {
        type = getType(value)
    }

    fun getType(value: String?) : CodeType {
        var ret : CodeType = CodeType.None
        if(value?.length == 13)
        {
            ret = CodeType.EAN13
        }
        return ret
    }
}