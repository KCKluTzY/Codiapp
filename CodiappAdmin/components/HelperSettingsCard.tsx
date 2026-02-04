import { View, Text, StyleSheet, Switch } from "react-native";
import Slider from "@react-native-community/slider";


export default function HelperSettingsCard() {
    return (
        <View style={styles.container}>
            <Text style={styles.sectionTitle}>Paramètres</Text>

            {/* Disponibilité */}
            <View style={styles.row}>
                <Text style={styles.label}>Disponible</Text>
                <Switch value />
            </View>

            {/* Rayon */}
            <View style={{ marginTop: 16 }}>
                <Text style={styles.label}>Rayon d’intervention (km)</Text>
                <Slider
                    minimumValue={1}
                    maximumValue={30}
                    value={10}
                    step={1}
                />
                <Text style={styles.distance}>10 km</Text>
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        backgroundColor: "#fff",
        margin: 16,
        padding: 16,
        borderRadius: 16,
    },
    sectionTitle: {
        fontSize: 16,
        fontWeight: "700",
        marginBottom: 12,
    },
    row: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
    },
    label: {
        fontSize: 14,
        fontWeight: "500",
    },
    distance: {
        textAlign: "center",
        marginTop: 4,
        fontWeight: "600",
    },
});
