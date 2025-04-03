package com.example.pmaapp.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pmaapp.ui.theme.FotGreen

@Composable
fun ReusableOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIconId: Int,
    modifier: Modifier = Modifier,
    leadingIconDescription: String = "",
    textColor: Color = Color.White,
    placeholderColor: Color = FotGreen,
    containerColor: Color = Color.DarkGray,
    borderColor: Color = FotGreen,
    unfocusedBorderColor: Color = Color.Gray,
    labelColor: Color = FotGreen,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp)
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = labelColor) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = leadingIconId),
                contentDescription = leadingIconDescription,
                tint = placeholderColor
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedPlaceholderColor = placeholderColor,
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            focusedBorderColor = borderColor,
            unfocusedBorderColor = unfocusedBorderColor,
            focusedLabelColor = labelColor,
        ),
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType, imeAction = imeAction
        ),
        shape = shape
    )
}
