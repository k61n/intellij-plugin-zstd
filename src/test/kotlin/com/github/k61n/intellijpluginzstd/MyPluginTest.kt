package com.github.k61n.intellijpluginzstd

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.psi.xml.XmlFile
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.PsiErrorElementUtil
import com.github.k61n.intellijpluginzstd.services.MyProjectService
import com.intellij.openapi.components.service
import kotlin.io.path.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.readText
import kotlin.io.path.writeText

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {

    fun testXMLFile() {
        val psiFile = myFixture.configureByText(XmlFileType.INSTANCE, "<foo>bar</foo>")
        val xmlFile = assertInstanceOf(psiFile, XmlFile::class.java)

        assertFalse(PsiErrorElementUtil.hasErrors(project, xmlFile.virtualFile))

        assertNotNull(xmlFile.rootTag)

        xmlFile.rootTag?.let {
            assertEquals("foo", it.name)
            assertEquals("bar", it.value.text)
        }
    }

    fun testRename() {
        myFixture.testRename("foo.xml", "foo_after.xml", "a2")
    }

    fun testZstdCompressionAndDecompression() {
        val service = project.service<MyProjectService>()

        val testContent = "test content"
        val testDir = createTempDirectory(prefix = "testZstd")
        val testFilename = testDir.resolve("compressionTest.txt")
        testFilename.writeText(testContent, Charsets.UTF_8)

        val compressedFilename = service.compressFile(testFilename.toString(), level = 3)
        assertTrue(Path(compressedFilename).toFile().exists())

        val decompressedFilename = service.decompressFile(compressedFilename)
        assertTrue(Path(decompressedFilename).toFile().exists())

        val decompressedContent = Path(decompressedFilename).readText(Charsets.UTF_8)
        assertEquals(testContent, decompressedContent)
    }

    override fun getTestDataPath() = "src/test/testData/rename"
}
