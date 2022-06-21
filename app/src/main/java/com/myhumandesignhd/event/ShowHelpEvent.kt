package com.myhumandesignhd.event

data class ShowHelpEvent(val type: HelpType)

enum class HelpType {
    BodygraphDecryption,
    BodygraphAddDiagram,
    BodygraphCenters
}