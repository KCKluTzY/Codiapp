import { ScrollView } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useLocalSearchParams } from "expo-router";

import HelperHeader from "@/components/HelperHeader";
import HelperStatsCard from "@/components/HelperStatsCard";
import HelperSettingsCard from "@/components/HelperSettingsCard";

export default function AdminHelperDetail() {
    const { id } = useLocalSearchParams<{ id: string }>();

    return (
        <SafeAreaView style={{ flex: 1 }}>
            <ScrollView>
                <HelperHeader helperId={id} />
                <HelperStatsCard />
                <HelperSettingsCard />
            </ScrollView>
        </SafeAreaView>
    );
}
