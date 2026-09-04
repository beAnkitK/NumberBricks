package io.github.beankitk.numberbricks.data

import kotlin.test.Test
import kotlin.test.assertEquals

class CornerProfileTest {

    @Test
    fun testGivenCornerTypes_createsProfile() {
        val profile =
            CornerProfile(
                topLeft = CornerType.Outer,
                topRight = CornerType.Edge,
                bottomRight = CornerType.Corner,
                bottomLeft = CornerType.Inner,
            )

        assertEquals(CornerType.Outer, profile.topLeft)
        assertEquals(CornerType.Edge, profile.topRight)
        assertEquals(CornerType.Corner, profile.bottomRight)
        assertEquals(CornerType.Inner, profile.bottomLeft)
    }

    @Test
    fun testGivenSameCornerType_createsUniformProfile() {
        val profile =
            CornerProfile(
                topLeft = CornerType.Joint,
                topRight = CornerType.Joint,
                bottomRight = CornerType.Joint,
                bottomLeft = CornerType.Joint,
            )

        assertEquals(CornerType.Joint, profile.topLeft)
        assertEquals(CornerType.Joint, profile.topRight)
        assertEquals(CornerType.Joint, profile.bottomRight)
        assertEquals(CornerType.Joint, profile.bottomLeft)
    }

    @Test
    fun testGivenEachCornerType_createsProfile() {
        val allTypes =
            listOf(
                CornerType.Outer,
                CornerType.Edge,
                CornerType.CornerNeighbor,
                CornerType.Corner,
                CornerType.JointInline,
                CornerType.Joint,
                CornerType.Inner,
            )

        allTypes.forEach { type ->
            val profile = CornerProfile(type, type, type, type)

            assertEquals(type, profile.topLeft)
            assertEquals(type, profile.topRight)
            assertEquals(type, profile.bottomRight)
            assertEquals(type, profile.bottomLeft)
        }
    }

    @Test
    fun testCopy_preservesAllCorners() {
        val original =
            CornerProfile(
                topLeft = CornerType.Outer,
                topRight = CornerType.Edge,
                bottomRight = CornerType.Corner,
                bottomLeft = CornerType.Inner,
            )

        val copied = original.copy()

        assertEquals(original.topLeft, copied.topLeft)
        assertEquals(original.topRight, copied.topRight)
        assertEquals(original.bottomRight, copied.bottomRight)
        assertEquals(original.bottomLeft, copied.bottomLeft)
    }

    @Test
    fun testCopy_replacesSpecifiedCorners() {
        val original =
            CornerProfile(
                topLeft = CornerType.Outer,
                topRight = CornerType.Outer,
                bottomRight = CornerType.Outer,
                bottomLeft = CornerType.Outer,
            )

        val modifiedTopLeft = original.copy(topLeft = CornerType.Edge)
        assertEquals(CornerType.Edge, modifiedTopLeft.topLeft)
        assertEquals(CornerType.Outer, modifiedTopLeft.topRight)
        assertEquals(CornerType.Outer, modifiedTopLeft.bottomRight)
        assertEquals(CornerType.Outer, modifiedTopLeft.bottomLeft)

        val modifiedMultiple =
            original.copy(topRight = CornerType.Joint, bottomLeft = CornerType.Inner)
        assertEquals(CornerType.Outer, modifiedMultiple.topLeft)
        assertEquals(CornerType.Joint, modifiedMultiple.topRight)
        assertEquals(CornerType.Outer, modifiedMultiple.bottomRight)
        assertEquals(CornerType.Inner, modifiedMultiple.bottomLeft)

        val modifiedAll =
            original.copy(
                topLeft = CornerType.Edge,
                topRight = CornerType.Corner,
                bottomRight = CornerType.JointInline,
                bottomLeft = CornerType.CornerNeighbor,
            )
        assertEquals(CornerType.Edge, modifiedAll.topLeft)
        assertEquals(CornerType.Corner, modifiedAll.topRight)
        assertEquals(CornerType.JointInline, modifiedAll.bottomRight)
        assertEquals(CornerType.CornerNeighbor, modifiedAll.bottomLeft)
    }

    @Test
    fun testToString_formatsCorrectly() {
        val profile =
            CornerProfile(
                topLeft = CornerType.Outer,
                topRight = CornerType.Edge,
                bottomRight = CornerType.Corner,
                bottomLeft = CornerType.Inner,
            )

        assertEquals(
            "CornerProfile(topLeft=Outer, topRight=Edge, bottomRight=Corner, bottomLeft=Inner)",
            profile.toString(),
        )
    }

    @Test
    fun testEqualProfiles_areEqual() {
        val profile1 =
            CornerProfile(
                topLeft = CornerType.Outer,
                topRight = CornerType.Edge,
                bottomRight = CornerType.Corner,
                bottomLeft = CornerType.Inner,
            )

        val profile2 =
            CornerProfile(
                topLeft = CornerType.Outer,
                topRight = CornerType.Edge,
                bottomRight = CornerType.Corner,
                bottomLeft = CornerType.Inner,
            )

        assertEquals(profile1, profile2)
    }
}
