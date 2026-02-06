import Slider from "@react-native-community/slider";
import { useState } from "react";
import { StyleSheet, Switch, Text, View } from "react-native";


export default function HelperSettingsCard() {
    const [distance, setDistance] = useState(10);

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
                    value={distance}
                    onValueChange={(val) => setDistance(val)}
                    step={1}
                />
                <Text style={styles.distance}>{distance} km</Text>
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
