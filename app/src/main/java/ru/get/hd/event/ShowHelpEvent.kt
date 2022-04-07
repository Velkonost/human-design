package ru.get.hd.event

import com.google.rpc.Help

data class ShowHelpEvent(val type: HelpType)

enum class HelpType {
    BodygraphDecryption,
    BodygraphAddDiagram,
    BodygraphCenters
}