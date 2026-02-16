import {
    ScrollView,
    Switch,
    View,
    Pressable,
    Text,
    StyleSheet,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import SettingSection from "@/components/SettingSection";
import SettingItem from "@/components/SettingItem";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useState } from "react";
import { Picker } from "@react-native-picker/picker";

type NumericControlProps = {
    value: number;
    min: number;
    max: number;
    onChange: (value: number) => void;
};

/* -------------------- */
/* Contrôle +/-         */
/* -------------------- */
const NumericControl: React.FC<NumericControlProps> = ({
    value,
    min,
    max,
    onChange,
}) => (
    <View style={styles.numericContainer}>
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

/* -------------------- */
/* Jours dispo          */
/* -------------------- */
const DAYS = [
    "Lundi",
    "Mardi",
    "Mercredi",
    "Jeudi",
    "Vendredi",
    "Samedi",
    "Dimanche",
];

export default function Parametres() {
    const router = useRouter();


    const [shareLocation, setShareLocation] = useState(false);
    const [emergencyNotifications, setEmergencyNotifications] =
        useState(false);
    const [normalNotifications, setNormalNotifications] =
        useState(false);
    const [darkMode, setDarkMode] = useState(false);

    const [timeStart, setTimeStart] = useState(9);
    const [timeEnd, setTimeEnd] = useState(17);
    const [fontSize, setFontSize] = useState(16);
    const [maxDistance, setMaxDistance] = useState(25);

    const [selectedDays, setSelectedDays] = useState<string[]>([]);
    const [fontFamily, setFontFamily] = useState("System");



    // Toggle d'un jour dans la sélection de jours disponibles
    const toggleDay = (day: string) => {
        setSelectedDays((prev) =>
            prev.includes(day)
                ? prev.filter((d) => d !== day)
                : [...prev, day]
        );
    };

   

    return (
        <SafeAreaView style={{ flex: 1 }}>
            <ScrollView>
                {/* HEADER */}
                <View style={styles.header}>
                    <Pressable
                        onPress={() => router.back()}
                        hitSlop={12}
                    >
                        <Ionicons
                            name="arrow-back"
                            size={26}
                            color="#111"
                        />
                    </Pressable>

                    <Text style={styles.headerTitle}>
                        Paramètres
                    </Text>
                </View>

                {/* AUTH */}
                <SettingSection title="Authentification">
                    <SettingItem
                        icon="person-outline"
                        label="S'authentifier"
                        onPress={() =>
                            router.push("/Authentification")
                        }
                    />
                </SettingSection>

                {/* LOCALISATION */}
                <SettingSection title="Localisation">
                    <SettingItem
                        icon="location"
                        label="Partager ma localisation"
                        right={
                            <Switch
                                value={shareLocation}
                                onValueChange={() =>
                                    setShareLocation((p) => !p)
                                }
                            />
                        }
                    />

                    {/* JOURS DISPO */}
                    <SettingItem
                        icon="calendar-outline"
                        label="Jours de disponibilité"
                        right={
                            <View style={styles.daysContainer}>
                                {DAYS.map((day) => {
                                    const selected =
                                        selectedDays.includes(day);

                                    return (
                                        <Pressable
                                            key={day}
                                            onPress={() =>
                                                toggleDay(day)
                                            }
                                            style={[
                                                styles.dayChip,
                                                selected &&
                                                styles.dayChipSelected,
                                            ]}
                                        >
                                            <Text
                                                style={[
                                                    styles.dayText,
                                                    selected &&
                                                    styles.dayTextSelected,
                                                ]}
                                            >
                                                {day.slice(0, 3)}
                                            </Text>
                                        </Pressable>
                                    );
                                })}
                            </View>
                        }
                    />

                    {/* PLAGE HORAIRE */}
                    <SettingItem
                        icon="time"
                        label={`Plage horaire: ${timeStart}h - ${timeEnd}h`}
                        right={
                            <View
                                style={{
                                    flexDirection: "row",
                                }}
                            >
                                <NumericControl
                                    value={timeStart}
                                    min={0}
                                    max={timeEnd - 1}
                                    onChange={setTimeStart}
                                />

                                <NumericControl
                                    value={timeEnd}
                                    min={timeStart + 1}
                                    max={24}
                                    onChange={setTimeEnd}
                                />
                            </View>
                        }
                    />
                </SettingSection>

                {/* STATS */}
                <SettingSection title="Statistiques">
                    <SettingItem
                        icon="stats-chart"
                        label="Voir mes statistiques"
                        onPress={() =>
                            router.push("/Statistiques")
                        }
                    />
                </SettingSection>

                {/* ACCESSIBILITÉ */}
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

                    {/* CHOIX POLICE */}
                    <SettingItem
                        icon="text-outline"
                        label="Choix de la police d’écriture"
                        right={
                            <View style={styles.pickerWrapper}>
                                <Picker
                                    selectedValue={fontFamily}
                                    style={styles.picker}
                                    onValueChange={(value) =>
                                        setFontFamily(value)
                                    }
                                >
                                    <Picker.Item
                                        label="Par défaut"
                                        value="Par défaut"
                                    />
                                    <Picker.Item
                                        label="Luciole"
                                        value="Luciole"
                                    />
                                </Picker>
                            </View>
                        }
                    />
                </SettingSection>

                {/* NOTIFS */}
                <SettingSection title="Notifications">
                    <SettingItem
                        icon="alert"
                        label="Urgences"
                        right={
                            <Switch
                                value={
                                    emergencyNotifications
                                }
                                onValueChange={() =>
                                    setEmergencyNotifications(
                                        (p) => !p
                                    )
                                }
                            />
                        }
                    />

                    <SettingItem
                        icon="notifications"
                        label="Demandes normales"
                        right={
                            <Switch
                                value={normalNotifications}
                                onValueChange={() =>
                                    setNormalNotifications(
                                        (p) => !p
                                    )
                                }
                            />
                        }
                    />
                </SettingSection>

                {/* GENERAL */}
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
                                onValueChange={() =>
                                    setDarkMode((p) => !p)
                                }
                            />
                        }
                    />
                </SettingSection>
            </ScrollView>
        </SafeAreaView>
    );
}

/* -------------------- */
/* STYLES               */
/* -------------------- */

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

    /* +/- */
    numericContainer: {
        flexDirection: "row",
        alignItems: "center",
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

    /* Days */
    daysContainer: {
        flexDirection: "row",
        flexWrap: "wrap",
        gap: 6,
        maxWidth: 200,
        justifyContent: "flex-end",
    },

    dayChip: {
        paddingHorizontal: 8,
        paddingVertical: 4,
        borderRadius: 8,
        backgroundColor: "#eee",
    },

    dayChipSelected: {
        backgroundColor: "#4db5ff",
    },

    dayText: {
        fontSize: 12,
        fontWeight: "600",
        color: "#333",
    },

    dayTextSelected: {
        color: "#fff",
    },

    /* Picker */
    pickerWrapper: {
        width: 160,
        height: 40,
        backgroundColor: "#eee",
        borderRadius: 8,
        justifyContent: "center",
    },

    picker: {
        width: "100%",
        height: 55,
    },
});
