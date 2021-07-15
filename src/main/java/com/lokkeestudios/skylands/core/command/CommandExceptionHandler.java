package com.lokkeestudios.skylands.core.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import com.lokkeestudios.skylands.core.utils.Constants;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.ComponentMessageThrowable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Function;

/**
 * Handles command {@link Exception}s of any type.
 * <p>
 * Creates {@link Component} Exception messages which are then
 * being passed into the {@link MinecraftExceptionHandler}.
 */
public final class CommandExceptionHandler<C> {

    /**
     * Registers the {@link CommandExceptionHandler} in the {@link CommandManager}.
     *
     * @param manager        the CommandManager instance
     * @param audienceMapper the Mapper which maps command sender to audience instances
     */
    public void apply(
            final @NonNull CommandManager<C> manager,
            final @NonNull Function<@NonNull C, @NonNull Audience> audienceMapper
    ) {
        new MinecraftExceptionHandler<C>()
                .withHandler(MinecraftExceptionHandler.ExceptionType.ARGUMENT_PARSING, this::argumentParsingHandler)
                .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SENDER, this::invalidSenderHandler)
                .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SYNTAX, this::invalidSyntaxHandler)
                .withHandler(MinecraftExceptionHandler.ExceptionType.NO_PERMISSION, this::noPermissionHandler)
                .withHandler(MinecraftExceptionHandler.ExceptionType.COMMAND_EXECUTION, this::commandExecutionHandler)
                .withDecorator(Constants.Text.PREFIX::append)
                .apply(manager, audienceMapper);
    }

    /**
     * Handles {@link Exception}s of type {@link MinecraftExceptionHandler.ExceptionType#ARGUMENT_PARSING}
     * and returns their error message {@link Component}.
     *
     * @param e the Exception which is to be handled
     * @return the message Component which should be output
     */
    private @NonNull Component argumentParsingHandler(final @NonNull Exception e) {
        final @Nullable Component message = ComponentMessageThrowable.getOrConvertMessage(e.getCause());

        return Component.text("Invalid command argument ", Constants.Text.STYLE_ALERT)
                .append((message == null ? Component.text("null") : message)
                        .colorIfAbsent(Constants.Text.COLOR_DEFAULT)
                );
    }

    /**
     * Handles {@link Exception}s of type {@link MinecraftExceptionHandler.ExceptionType#INVALID_SENDER}
     * and returns their error message {@link Component}.
     *
     * @param e the Exception which is to be handled
     * @return the message Component which should be output
     */
    private @NonNull Component invalidSenderHandler(final @NonNull Exception e) {
        final @NonNull InvalidCommandSenderException invalidSenderException = (InvalidCommandSenderException) e;

        return Component.text("Invalid command sender. You must be of type ", Constants.Text.STYLE_ALERT)
                .append(Component.text(
                        invalidSenderException.getRequiredSender().getSimpleName(), Constants.Text.STYLE_DEFAULT
                ));
    }

    /**
     * Handles {@link Exception}s of type {@link MinecraftExceptionHandler.ExceptionType#INVALID_SYNTAX}
     * and returns their error message {@link Component}.
     *
     * @param e the Exception which is to be handled
     * @return the message Component which should be output
     */
    private @NonNull Component invalidSyntaxHandler(final @NonNull Exception e) {
        final @NonNull InvalidSyntaxException invalidSyntaxException = (InvalidSyntaxException) e;

        return Component.text("Invalid command syntax. The correct command syntax is ", Constants.Text.STYLE_ALERT)
                .append(Component.text(
                        String.format("/%s", invalidSyntaxException.getCorrectSyntax()), Constants.Text.STYLE_DEFAULT
                ));
    }

    /**
     * Handles {@link Exception}s of type {@link MinecraftExceptionHandler.ExceptionType#NO_PERMISSION}
     * and returns their error message {@link Component}.
     *
     * @param e the Exception which is to be handled
     * @return the message Component which should be output
     */
    private @NonNull Component noPermissionHandler(final @NonNull Exception e) {
        final @NonNull NoPermissionException noPermissionException = (NoPermissionException) e;

        return Component.text("No Permission. Requires the permission ", Constants.Text.STYLE_ALERT)
                .append(Component.text(
                        noPermissionException.getMissingPermission(), Constants.Text.STYLE_DEFAULT
                ));
    }

    /**
     * Handles {@link Exception}s of type {@link MinecraftExceptionHandler.ExceptionType#COMMAND_EXECUTION}
     * and returns their error message {@link Component}.
     *
     * @param e the Exception which is to be handled
     * @return the message Component which should be output
     */
    private @NonNull Component commandExecutionHandler(final @NonNull Exception e) {
        return Component.text(
                "An unexpected error occurred during command execution.", Constants.Text.STYLE_ALERT
        );
    }
}
