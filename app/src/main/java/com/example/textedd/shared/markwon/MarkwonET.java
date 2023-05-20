package com.example.textedd.shared.markwon;

import android.content.Context;
import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.textedd.shared.markwon.handlers.BlockQuoteEditHandler;
import com.example.textedd.shared.markwon.handlers.CodeEditHandler;
import com.example.textedd.shared.markwon.handlers.HeadingEditHandler;
import com.example.textedd.shared.markwon.handlers.LinkEditHandler;
import com.example.textedd.shared.markwon.handlers.StrikethroughEditHandler;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Node;
import org.commonmark.parser.InlineParserFactory;
import org.commonmark.parser.Parser;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.SoftBreakAddsNewLinePlugin;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.editor.handler.EmphasisEditHandler;
import io.noties.markwon.editor.handler.StrongEmphasisEditHandler;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.inlineparser.BangInlineProcessor;
import io.noties.markwon.inlineparser.EntityInlineProcessor;
import io.noties.markwon.inlineparser.HtmlInlineProcessor;
import io.noties.markwon.inlineparser.InlineProcessor;
import io.noties.markwon.inlineparser.MarkwonInlineParser;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.inlineparser.OpenBracketInlineProcessor;
import io.noties.markwon.linkify.LinkifyPlugin;
import kotlin.jvm.internal.Intrinsics;

public class MarkwonET {

    public static void render(Context context, EditText mEditText) {

        // for links to be clickable
        mEditText.setMovementMethod(LinkMovementMethod.getInstance());

        final InlineParserFactory inlineParserFactory = MarkwonInlineParser.factoryBuilder()
                // no inline images will be parsed
                .excludeInlineProcessor(BangInlineProcessor.class)
                // no html tags will be parsed
                .excludeInlineProcessor(HtmlInlineProcessor.class)
                // no entities will be parsed (aka `&amp;` etc)
                .excludeInlineProcessor(EntityInlineProcessor.class)
                .build();
        final Markwon markwon = Markwon.builder(context)
                .usePlugin(MarkwonInlineParserPlugin.create(factoryBuilder ->
                        factoryBuilder
                                .addInlineProcessor(new MyTextInlineProcessor())
                                .excludeInlineProcessor(OpenBracketInlineProcessor.class)))
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(LinkifyPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureParser(@NonNull Parser.Builder builder) {

                        // disable all commonmark-java blocks, only inlines will be parsed
//          builder.enabledBlockTypes(Collections.emptySet());

                        builder.inlineParserFactory(inlineParserFactory);
                    }
                    @Override
                    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
                        builder
                                .on(MyTextNode.class, new GenericInlineNodeVisitor())
                                .on(NotMyTextNode.class, new GenericInlineNodeVisitor());
                    }
                    @Override
                    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                        builder
                                .setFactory(MyTextNode.class, (configuration, props) -> new ForegroundColorSpan(Color.GREEN));
                        //.setFactory(NotMyTextNode.class, (configuration, props) -> new ForegroundColorSpan(Color.GREEN));
                    }
                })
                .usePlugin(SoftBreakAddsNewLinePlugin.create())
                .build();

        final LinkEditHandler.OnClick onClick =  (widget, link) -> {
            markwon.configuration().linkResolver().resolve(widget, link);
            //textView.setText(dialogInput);
            //createFile(link);
            //openFile(link);
        };

        final MarkwonEditor editor = MarkwonEditor.builder(markwon)
                .useEditHandler(new EmphasisEditHandler())
                .useEditHandler(new StrongEmphasisEditHandler())
                .useEditHandler(new StrikethroughEditHandler())
                .useEditHandler(new CodeEditHandler())
                .useEditHandler(new BlockQuoteEditHandler())
                .useEditHandler(new LinkEditHandler(onClick))
                .useEditHandler(new HeadingEditHandler())
                .build();
        markwon.setMarkdown(mEditText, mEditText.getText().toString());
        mEditText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                editor, Executors.newSingleThreadExecutor(), mEditText));
    }
    /*
    public static void addSpan(TextView textView, Object... spans) {
        SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText());
        int end = builder.length();
        //int var8 = spans.length;
        for (Object span : spans) {
            builder.setSpan(span, 0, end, 33);
        }
        textView.setText((CharSequence)builder);
    }*/

    public static final class InsertOrWrapClickListener implements View.OnClickListener {
        private final EditText editText;
        private final String text;
        public void onClick(@NotNull View v) {
            Intrinsics.checkNotNullParameter(v, "v");
            int start = this.editText.getSelectionStart();
            int end = this.editText.getSelectionEnd();
            if (start >= 0) {
                if (start == end) {
                    this.editText.getText().insert(start, (CharSequence)this.text);
                } else {
                    this.editText.getText().insert(end, (CharSequence)this.text);
                    this.editText.getText().insert(start, (CharSequence)this.text);
                }
            }
        }
        public InsertOrWrapClickListener(@NotNull EditText editText, @NotNull String text) {
            Intrinsics.checkNotNullParameter(editText, "editText");
            Intrinsics.checkNotNullParameter(text, "text");
            this.editText = editText;
            this.text = text;
        }
    }

    private static class GenericInlineNodeVisitor implements MarkwonVisitor.NodeVisitor<Node> {
        @Override
        public void visit(@NonNull MarkwonVisitor visitor, @NonNull Node node) {
            final int length = visitor.length();
            visitor.visitChildren(node);
            visitor.setSpansForNodeOptional(node, length);
        }
    }

    private static class MyTextInlineProcessor extends InlineProcessor {

        private static final Pattern RE = Pattern.compile("\\[\\[(.+?)\\]\\]");

        @Override
        public char specialCharacter() {
            return '[';
        }

        @Nullable
        @Override
        protected Node parse() {
            final String match = match(RE);
            //Debug.i(match);
            if (match != null) {
                // consume syntax
                final String text = match.substring(2, match.length() - 2);
                final Node node;
                node = new MyTextNode();
                // for example some condition checking
                /*
                if (text.equals("my text")) {
                    node = new MyTextNode();
                } else {
                    node = new NotMyTextNode();
                }*/
                node.appendChild(text(text));
                return node;
            }
            return null;
        }
    }

    private static class MyTextNode extends CustomNode {
    }

    private static class NotMyTextNode extends CustomNode {
    }
}

