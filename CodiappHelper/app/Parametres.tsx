import { ScrollView, Switch, View, Pressable, Text, StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import SettingSection from "@/components/SettingSection";
import SettingItem from "@/components/SettingItem";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useState } from "react";

type NumericControlProps = {
    value: number;
    min: number;
    max: number;
    onChange: (value: number) => void;
};

// Contrôle numérique +/- 
const NumericControl: React.FC<NumericControlProps> = ({ value, min, max, onChange }) => (
    <View style={{ flexDirection: "row", alignItems: "center", marginHorizontal: 4 }}>
        <Pressable
            onPress={() => onChange(Math.max(min, value - 1))}
            style={styles.button}
        >
            <Text style={styles.buttonText}>-</Text>
        </Pressable>
        <Text style={{ marginHorizontal: 6 }}>{value}</Text>
        <Pressable
            onPress={() => onChange(Math.min(max, value + 1))}
            style={styles.button}
        >
            <Text style={styles.buttonText}>+</Text>
        </Pressable>
    </View>
);

export default function Parametres() {
    const router = useRouter();

    // États des Switch
    const [shareLocation, setShareLocation] = useState(false);
    const [emergencyNotifications, setEmergencyNotifications] = useState(false);
    const [normalNotifications, setNormalNotifications] = useState(false);
    const [darkMode, setDarkMode] = useState(false);

    // Etats des valeurs Numérique(compteur)
    const [timeStart, setTimeStart] = useState(9);  // Heure de début
    const [timeEnd, setTimeEnd] = useState(17);     // Heure de fin
    const [fontSize, setFontSize] = useState(16);   // Taille du texte
    const [maxDistance, setMaxDistance] = useState(25); // Distance en km 

    return (
        <SafeAreaView style={{ flex: 1 }}>
            <ScrollView>
                <View style={styles.header}>
                    <Pressable onPress={() => router.back()} hitSlop={12}>
                        <Ionicons name="arrow-back" size={26} color="#111" />
                    </Pressable>
                    <Text style={styles.headerTitle}>Paramètres</Text>
                </View>

                <SettingSection title="Authentification">
                    <SettingItem
                        icon="person-outline"
                        label="S'authentifier"
                        onPress={() => router.push("/Authentification")}
                        right={null}
                    />
                </SettingSection>

                <SettingSection title="Localisation">
                    <SettingItem
                        icon="location"
                        label="Partager ma localisation"
                        right={
                            <Switch
                                value={shareLocation}
                                onValueChange={() => setShareLocation(prev => !prev)}
                            />
                        }
                    />
                    <SettingItem
                        icon="time"
                        label={`Plage horaire: ${timeStart}h - ${timeEnd}h`}
                        right={
                            <View style={{ flexDirection: "row" }}>
                                <NumericControl
                                    value={timeStart}
                                    min={0}
                                    max={timeEnd - 1} // Heure de début ne peut pas dépasser heure de fin
                                    onChange={setTimeStart}
                                />
                                <NumericControl
                                    value={timeEnd}
                                    min={timeStart + 1} // Heure de fin ne peut pas descendre avant heure de début
                                    max={24}
                                    onChange={setTimeEnd}
                                />
                            </View>
                        }
                    />
                </SettingSection>


                <SettingSection title="Statistiques">
                    <SettingItem
                        icon="stats-chart"
                        label="Voir mes statistiques"
                        onPress={() => router.push("/Statistiques")}
                        right={null}
                    />
                </SettingSection>


                <SettingSection title="Accessibilité">
                    <SettingItem
                        icon="text"
                        label={`Taille du texte: ${fontSize}`}
                        right={
                            <NumericControl
                                value={fontSize}
                                min={12}
                                max={32}
                                onChange={setFontSize}
                            />
                        }
                    />
                </SettingSection>


                <SettingSection title="Notifications">
                    <SettingItem
                        icon="alert"
                        label="Urgences"
                        right={
                            <Switch
                                value={emergencyNotifications}
                                onValueChange={() => setEmergencyNotifications(prev => !prev)}
                            />
                        }
                    />
                    <SettingItem
                        icon="notifications"
                        label="Demandes normales"
                        right={
                            <Switch
                                value={normalNotifications}
                                onValueChange={() => setNormalNotifications(prev => !prev)}
                            />
                        }
                    />
                </SettingSection>


                <SettingSection title="Général">
                    <SettingItem
                        icon="map"
                        label={`Distance maximale: ${maxDistance} km`}
                        right={
                            <NumericControl
                                value={maxDistance}
                                min={0}
                                max={50}
                                onChange={setMaxDistance}
                            />
                        }
                    />
                    <SettingItem
                        icon="moon"
                        label="Mode sombre"
                        right={
                            <Switch
                                value={darkMode}
                                onValueChange={() => setDarkMode(prev => !prev)}
                            />
                        }
                    />
                </SettingSection>
            </ScrollView>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    header: {
        height: 56,
        justifyContent: "center",
        paddingHorizontal: 16,
    },
    headerTitle: {
        position: "absolute",
        alignSelf: "center",
        fontSize: 20,
        fontWeight: "700",
    },
    button: {
        paddingHorizontal: 10,
        paddingVertical: 2,
        backgroundColor: "#ddd",
        borderRadius: 4,
    },
    buttonText: {
        fontSize: 16,
        fontWeight: "bold",
    },
});
