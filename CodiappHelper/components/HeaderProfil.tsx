import { View, Text, StyleSheet, Image, Pressable } from "react-native";
import { Colors } from "../constants/Colors";
import { MaterialIcons } from "@expo/vector-icons";
import StatsRow from "./StatsRow";

export default function HeaderProfile() {
    return (
        <View style={styles.container}>
            <View style={styles.left}>
                <View style={styles.avatar}>
                    <Text style={styles.avatarText}>M</Text>
                </View>

                <View>
                    <Text style={styles.name}>Marie Dupont</Text>
                    <Text style={styles.role}>Aidante</Text>
                </View>
            </View>

            <Pressable style={styles.settingsButton}>
                <MaterialIcons name="settings" size={28} color="white" />
            </Pressable>
            <StatsRow />
        </View>
    );
}
const styles = StyleSheet.create({
    container: {
        backgroundColor: "#9B4DFF",
        paddingTop: 48,
        paddingHorizontal: 20,
        paddingBottom: 24,
        borderBottomLeftRadius: 24,
        borderBottomRightRadius: 24,
    },

    topRow: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        marginBottom: 20,
    },

    left: {
        flexDirection: "row",
        alignItems: "center",
    },

    avatar: {
        width: 48,
        height: 48,
        borderRadius: 24,
        backgroundColor: "white",
        alignItems: "center",
        justifyContent: "center",
        marginRight: 12,
    },

    avatarText: {
        color: "#9B4DFF",
        fontSize: 20,
        fontWeight: "700",
    },

    name: {
        color: "white",
        fontSize: 16,
        fontWeight: "700",
    },

    role: {
        color: "white",
        fontSize: 13,
        fontWeight: "700",
    },
    settingsButton: {
        position: "absolute",
        top: 48,
        right: 20,
    },
});
